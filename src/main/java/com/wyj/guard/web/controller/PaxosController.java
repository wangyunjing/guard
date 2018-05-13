package com.wyj.guard.web.controller;

import com.wyj.guard.bootstrap.paxos.Acceptor;
import com.wyj.guard.bootstrap.paxos.Vote;
import com.wyj.guard.bootstrap.paxos.VotingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaxosController {

    @Autowired
    Acceptor acceptor;

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
     * 1) 续租成功
     * 2) 续租失败
     * 2.1 - 该实例已经不是Master : 续租方重新投票选举
     * 2.2 - 请求失败
     * 2.3 - 续租时的回合数不等于该实例的回合数
     * 2.3.1 - 小于该实例的回合数 : 返回续租时的回合数的值
     * 2.3.2 - 大于该实例的回合数 : 续租方重新投票选举, 本实例注销Master并重新投票选举
     */
    @PutMapping("/paxos/lease")
    public Object lease(@RequestParam("instanceId") String instanceId) {
        // TODO: 2018/5/14
        return null;
    }

    /**
     * 判断是否为主节点，如果instanceId is null，那么判断自身
     */
    @GetMapping("/paxos/is_master")
    public Object isMaster(@RequestParam(value = "instanceId", required = false)
                                   String instanceId) {
        // TODO: 2018/5/14
        return null;
    }
}
