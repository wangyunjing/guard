package com.wyj.guard.bootstrap.paxos;

public interface Lease {

    LeaseResult lease(Long round, String instanceId);

}
