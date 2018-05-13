package com.wyj.guard.bootstrap.paxos;

import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class Paxos implements Proposer, Acceptor {

    // 全局的回合数
    private AtomicLong round = new AtomicLong(0);

    // 所有的实例
    private BoundedCollection<PaxosInstance> paxosInstances;

    // 当前状态
    private volatile PaxosStatus status = PaxosStatus.LOOKING;

    // 自身实例 - instanceId
    private String ownInstance;

    private Supplier<Boolean> callback;

    private PaxosCommunications communications;

    public Paxos(RestTemplate restTemplate,
                 int paxosInstanceNum,
                 String ownInstance,
                 List<String> allInstances,
                 Supplier<Boolean> callback) {
        if (paxosInstanceNum < 10 && paxosInstanceNum >= 0) {
            paxosInstanceNum = 10;
        }
        this.ownInstance = ownInstance;
        this.callback = callback;
        communications = new PaxosCommunications(restTemplate, allInstances);
        paxosInstances = new BoundedCollection<>(paxosInstanceNum);
    }

    @Override
    public PaxosStatus propose() {
        while (true) {
            // 增加回合数
            round.incrementAndGet();
            // 改变状态为Looking
            status = PaxosStatus.LOOKING;
            // 真正发起投票
            PaxosInstance instance = new PaxosInstance(round.get(), ownInstance, communications);
            paxosInstances.add(instance);
            status = instance.propose();
            switch (status) {
                case LOOKING:
                    // 继续发起下一回合的提议
                    break;
                case LEADER:
                    // 是主节点
                    // 1 : 定时维护其他实例的租约
                    defendLeaseWithLeader();
                    return status;
                case FOLLOWER:
                    // 不是主节点
                    // 1 : 定时向主节点发起租约
                    defendLeaseWithFollower();
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
        VotingResult result = instance.acceptPhase(vote);
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

    // 主节点维护租约
    public void defendLeaseWithLeader() {
        // TODO: 2018/5/14
    }

    // 从节点维护租约
    public void defendLeaseWithFollower() {
        // TODO: 2018/5/14
    }

    public void setAllInstances(List<String> allInstances) {
        this.communications.setAllInstances(allInstances);
    }
}
