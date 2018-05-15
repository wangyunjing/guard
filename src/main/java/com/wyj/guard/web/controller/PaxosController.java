package com.wyj.guard.web.controller;

import com.wyj.guard.bootstrap.BootStrap;
import com.wyj.guard.bootstrap.paxos.Acceptor;
import com.wyj.guard.bootstrap.paxos.Lease;
import com.wyj.guard.bootstrap.paxos.Vote;
import com.wyj.guard.bootstrap.paxos.VotingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaxosController {

    @Autowired
    Acceptor acceptor;

    @Autowired
    Lease lease;

    @Autowired
    BootStrap bootStrap;

    /**
     * 准备阶段
     */
    @PutMapping("/paxos/prepare")
    public VotingResult preparePhase(@RequestBody Vote vote) {
        return acceptor.preparePhase(vote);
    }

    /**
     * 接受阶段
     */
    @PutMapping("/paxos/accept")
    public VotingResult acceptPhase(@RequestBody Vote vote) {
        return acceptor.acceptPhase(vote);
    }

    /**
     * 续租(Follower -> Leader)
     */
    @PutMapping("/paxos/lease")
    public Object lease(@RequestParam("round") Long round,
                        @RequestParam("instanceId") String instanceId) {
        return lease.lease(round, instanceId);
    }

    /**
     * 判断是否为主节点，如果instanceId is null，那么判断自身
     */
    @GetMapping("/paxos/is_master")
    public Object isMaster(@RequestParam(value = "instanceId", required = false)
                                   String instanceId) {

        return bootStrap.isMaster(instanceId);
    }
}
