package com.wyj.guard.bootstrap.paxos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PaxosInstance implements Proposer, Acceptor {

    private Logger logger = LoggerFactory.getLogger(PaxosInstance.class);

    // 回合数
    private final long round;

    // 通信
    private final PaxosCommunications communications;

    // 提议者
    private final Proposer0 proposer;

    // 接收者
    private final Acceptor0 acceptor;

    // 真正确定的主节点的值
    private String master;

    public PaxosInstance(long round, String value, PaxosCommunications communications) {
        this.round = round;
        this.communications = communications;
        proposer = new Proposer0(value);
        acceptor = new Acceptor0();
    }

    @Override
    public PaxosStatus propose() {
        return proposer.propose();
    }

    @Override
    public VotingResult preparePhase(Vote vote) {
        return acceptor.preparePhase(vote);
    }

    @Override
    public VotingResult acceptPhase(Vote vote) {
        return acceptor.acceptPhase(vote);
    }

    // 提议者
    private class Proposer0 implements Proposer {
        private AtomicLong proposedNum;

        private final String value;

        public Proposer0(String value) {
            proposedNum = new AtomicLong(0);
            this.value = value;
        }

        @Override
        public PaxosStatus propose() {
            while (true) {
                Vote vote = new Vote();
                vote.setRound(round);
                vote.setProposedNum(proposedNum.incrementAndGet());

                List<VotingResult> results = communications.first(vote);
                int passNum = 0;
                long maxAcceptProposedNum = -1;
                String curValue = value;
                for (VotingResult votingResult : results) {
                    if (votingResult == null) {
                        continue;
                    }
                    if (votingResult.getRoundComparison() < 0) {
                        // 确定主节点
                        master = votingResult.getMaster();
                        return PaxosStatus.LOOKING;
                    } else if (votingResult.getRoundComparison() > 0) {
                        continue;
                    }
                    if (votingResult.isPassing()) {
                        // 通过
                        passNum++;
                        if (votingResult.getAcceptProposedNum() != null &&
                                votingResult.getAcceptValue() != null &&
                                votingResult.getAcceptProposedNum() > maxAcceptProposedNum) {
                            maxAcceptProposedNum = votingResult.getAcceptProposedNum();
                            curValue = votingResult.getAcceptValue();
                        }
                    }
                }
                // 小于等于半数 : 准备阶段，投票不通过
                if (passNum <= communications.getInstanceNum() / 2) {
                    waitTime();
                    continue;
                }
                // 大于半数 : 准备阶段，投票通过
                vote.setValue(curValue);
                results = communications.second(vote);
                passNum = 0;
                for (VotingResult votingResult : results) {
                    if (votingResult == null) {
                        continue;
                    }
                    if (votingResult.getRoundComparison() < 0) {
                        // 确定主节点
                        master = votingResult.getMaster();
                        return PaxosStatus.LOOKING;
                    } else if (votingResult.getRoundComparison() > 0) {
                        continue;
                    }
                    if (votingResult.isPassing()) {
                        passNum++;
                    }
                }
                // 大于半数 : 接受阶段，投票通过
                if (passNum > communications.getInstanceNum() / 2) {
                    // 确定主节点
                    master = curValue;
                    if (master.equals(value)) {
                        return PaxosStatus.LEADER;
                    }
                    return PaxosStatus.FOLLOWER;
                }
                // 小于半数 : 接受阶段，投票不通过。继续投票...
                waitTime();
            }
        }
    }

    // 接受者
    private class Acceptor0 implements Acceptor {
        private AtomicLong proposedNum;

        private AtomicLong acceptProposedNum;

        private String acceptValue;

        public Acceptor0() {
            proposedNum = new AtomicLong(0);
        }

        public VotingResult preparePhase(Vote vote) {
            VotingResult result = new VotingResult();
            if (vote.getProposedNum() <= proposedNum.get()) {
                result.setPassing(false);
                return result;
            }
            proposedNum.set(vote.getProposedNum());
            result.setPassing(true);
            if (acceptProposedNum != null) {
                result.setAcceptProposedNum(acceptProposedNum.get());
            }
            if (acceptValue != null) {
                result.setAcceptValue(acceptValue);
            }
            return result;
        }

        public VotingResult acceptPhase(Vote vote) {
            VotingResult result = new VotingResult();
            if (vote.getProposedNum() < proposedNum.get()) {
                result.setPassing(false);
                return result;
            }
            acceptProposedNum.set(vote.getProposedNum());
            acceptValue = vote.getValue();
            result.setPassing(true);
            return result;
        }
    }

    private void waitTime() {
        // 在[500,1750]之间
        long time = (long) ((Math.random() + 0.4) * 10000) / 8;
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            //
        }
    }

    public String getMaster() {
        return master;
    }

    public long getRound() {
        return round;
    }
}
