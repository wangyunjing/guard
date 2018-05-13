package com.wyj.guard.bootstrap.paxos;

public class Vote {

    // 投票方的回合数
    private long round;

    // 投票方的投票编号
    private long proposedNum;

    // 投票方的投票值
    private String value;

    public long getRound() {
        return round;
    }

    public void setRound(long round) {
        this.round = round;
    }

    public long getProposedNum() {
        return proposedNum;
    }

    public void setProposedNum(long proposedNum) {
        this.proposedNum = proposedNum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
