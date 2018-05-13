package com.wyj.guard.bootstrap.paxos;

public interface Acceptor {

    VotingResult preparePhase(Vote vote);

    VotingResult acceptPhase(Vote vote);
}
