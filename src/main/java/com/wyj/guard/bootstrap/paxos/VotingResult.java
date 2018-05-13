package com.wyj.guard.bootstrap.paxos;

public class VotingResult {
    /**
     * < 0 : 投票方的回合数小于接收方的回合数
     * = 0 : 投票方的回合数相等于接收方的回合数
     * > 0 : 投票方的回合数大于接收方的回合数
     */
    private long roundComparison;

    // 投票方的回合数
    private long round;

    // 接受方默认的投票结果 : 接收方的实例ID
    private String defaultValue;

    // 当前投票是否通过
    private boolean passing;

    // 接收方当前已接受的投票编号
    private Long acceptProposedNum;

    // 接收方当前已接受的投票结果
    private String acceptValue;

    // 接收方已经确定的主节点的值
    private String master;

    public long getRoundComparison() {
        return roundComparison;
    }

    public void setRoundComparison(long roundComparison) {
        this.roundComparison = roundComparison;
    }

    public long getRound() {
        return round;
    }

    public void setRound(long round) {
        this.round = round;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isPassing() {
        return passing;
    }

    public void setPassing(boolean passing) {
        this.passing = passing;
    }

    public Long getAcceptProposedNum() {
        return acceptProposedNum;
    }

    public void setAcceptProposedNum(Long acceptProposedNum) {
        this.acceptProposedNum = acceptProposedNum;
    }

    public String getAcceptValue() {
        return acceptValue;
    }

    public void setAcceptValue(String acceptValue) {
        this.acceptValue = acceptValue;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
