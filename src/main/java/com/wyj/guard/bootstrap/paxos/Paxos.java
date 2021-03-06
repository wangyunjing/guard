package com.wyj.guard.bootstrap.paxos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Paxos implements Proposer, Acceptor, Lease {

    private Logger logger = LoggerFactory.getLogger(Paxos.class);

    // 全局的回合数
    private AtomicLong round = new AtomicLong(0);

    // 所有的实例
    private BoundedCollection<PaxosInstance> paxosInstances;

    private BoundedCollection<Node> nodes;

    // 当前状态
    private volatile PaxosStatus status = PaxosStatus.LOOKING;

    // 自身实例 - instanceId
    private String ownInstance;

    private Supplier<CompletableFuture<Boolean>> callback;

    private PaxosCommunications communications;

    public Paxos(RestTemplate restTemplate,
                 int paxosInstanceNum,
                 String ownInstance,
                 List<String> allInstances,
                 Supplier<CompletableFuture<Boolean>> callback) {
        if (paxosInstanceNum < 10 && paxosInstanceNum >= 0) {
            paxosInstanceNum = 10;
        }
        this.ownInstance = ownInstance;
        this.callback = callback;
        communications = new PaxosCommunications(restTemplate, allInstances);
        paxosInstances = new BoundedCollection<>(paxosInstanceNum);
        nodes = new BoundedCollection<>(paxosInstanceNum);
    }

    public void setAllInstances(List<String> allInstances) {
        this.communications.setAllInstances(allInstances);
    }

    public Object isMaster(String instanceId) {
        if (instanceId == null) {
            instanceId = ownInstance;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("instanceId", instanceId);
        PaxosInstance last = paxosInstances.getLast();
        if (last != null) {
            map.put("master", last.getMaster());
        } else {
            map.put("master", "-1");
        }
        map.put("status", status);
        if (instanceId.equals(map.get("master"))) {
            map.put("isMaster", true);
        } else {
            map.put("isMaster", false);
        }
        map.put("round", round.get());
        map.put("nodes", nodes.getAllData());
        map.put("instances", paxosInstances.getAllData());
        return map;
    }

    // 同步 - 由调用方保证
    @Override
    public PaxosStatus propose() {
        while (true) {
            // 增加回合数
            round.incrementAndGet();
            // 改变状态为Looking
            status = PaxosStatus.LOOKING;
            Node node = nodes.getLast();
            if (node != null) {
                node.destroy();
            }
            // 真正发起投票
            PaxosInstance instance = new PaxosInstance(round.get(), ownInstance, communications);
            paxosInstances.add(instance);
            status = instance.propose();
            switch (status) {
                case LOOKING:
                    // 继续发起下一回合的提议
                    synchronized (nodes) {
                        if (instance.getMaster().equals(ownInstance)) {
                            Master master = new Master(round.get(), ownInstance, getNodesExcludeOwn());
                            nodes.add(master);
                        } else {
                            Slave slave = new Slave(round.get(), ownInstance, instance.getMaster());
                            nodes.add(slave);
                        }
                    }
                    break;
                case LEADER:
                    // 是主节点
                    // 1 : 定时维护其他实例的租约
                    synchronized (nodes) {
                        Master master = new Master(round.get(), ownInstance, this::renewPropose,
                                getNodesExcludeOwn());
                        nodes.add(master);
                    }
                    return status;
                case FOLLOWER:
                    // 不是主节点
                    // 1 : 定时向主节点发起租约
                    synchronized (nodes) {
                        Slave slave = new Slave(round.get(), ownInstance, this::renewPropose,
                                instance.getMaster(), communications);
                        nodes.add(slave);
                    }
                    return status;
            }
        }
    }

    @Override
    public synchronized VotingResult preparePhase(Vote vote) {
        VotingResult votingResult = new VotingResult();
        votingResult.setRoundComparison(vote.getRound() - round.get());
        if (votingResult.getRoundComparison() > 0) {
            return votingResult;
        }
        PaxosInstance instance = paxosInstances.get(paxosInstance -> vote.getRound() == paxosInstance.getRound());
        if (instance == null) {
            return null;
        }
        if (votingResult.getRoundComparison() < 0) {
            votingResult.setMaster(instance.getMaster());
            return votingResult;
        }
        VotingResult result = instance.preparePhase(vote);
        result.setRoundComparison(votingResult.getRoundComparison());
        result.setDefaultValue(ownInstance);
        result.setRound(vote.getRound());
        return result;
    }

    @Override
    public synchronized VotingResult acceptPhase(Vote vote) {
        VotingResult votingResult = new VotingResult();
        votingResult.setRoundComparison(vote.getRound() - round.get());
        votingResult.setDefaultValue(ownInstance);
        votingResult.setRound(vote.getRound());
        if (votingResult.getRoundComparison() > 0) {
            return votingResult;
        }
        PaxosInstance instance = paxosInstances.get(paxosInstance -> vote.getRound() == paxosInstance.getRound());
        if (instance == null) {
            return null;
        }
        if (votingResult.getRoundComparison() < 0) {
            votingResult.setMaster(instance.getMaster());
            return votingResult;
        }
        VotingResult result = instance.acceptPhase(vote);
        result.setRoundComparison(votingResult.getRoundComparison());
        result.setDefaultValue(ownInstance);
        result.setRound(vote.getRound());
        return result;
    }

    @Override
    public LeaseResult lease(Long leaseRound, String instanceId) {
        LeaseResult leaseResult = new LeaseResult();
        long roundComparison = leaseRound - round.get();
        leaseResult.setRoundComparison(roundComparison);
        if (roundComparison != 0) {
            logger.debug("lease roundComparison：{}", roundComparison);
            return leaseResult;
        }
        synchronized (nodes) {
            logger.debug("leaseRound : {}, instanceId : {}", leaseResult, instanceId);
            Node curNode = nodes.get(node -> leaseRound.equals(node.getRound()));
            if (curNode != null) {
                Master master = (Master) curNode;
                master.lease(leaseRound, instanceId);
            }
        }
        return leaseResult;
    }

    private void renewPropose(long leaseRound) {
        if (round.get() != leaseRound) {
            return;
        }
        logger.warn("leaseRound :{}, 重新投票选举主节点...", leaseRound);
        // TODO: 2018/5/14 维护异步处理结果
        CompletableFuture<Boolean> future = callback.get();
    }

    private List<String> getNodesExcludeOwn() {
        List<String> allInstances = communications.getAllInstances();
        return allInstances.stream()
                .filter(instanceId -> !instanceId.equals(ownInstance))
                .collect(Collectors.toList());
    }
}

