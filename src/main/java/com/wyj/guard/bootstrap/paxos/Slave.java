package com.wyj.guard.bootstrap.paxos;

import com.wyj.guard.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

// 从节点
public class Slave extends Node {

    private Logger logger = LoggerFactory.getLogger(Slave.class);

    // 上一次维护租约失败的时间
    private AtomicLong lastTime = new AtomicLong(-1);

    private PaxosCommunications communications;

    private String master;

    public Slave(long round, String ownInstanceId, Consumer<Long> callback, String master,
                 PaxosCommunications communications) {
        super(round, ownInstanceId, callback);
        this.communications = communications;
        this.master = master;
        scheduled.schedule(new FollowerOfLeaseTask(), 0, TimeUnit.MILLISECONDS);
    }

    public Slave(long round, String ownInstanceId, String master) {
        super(round, ownInstanceId, null);
        this.master = master;
    }

    // 从节点维护租约的任务
    private class FollowerOfLeaseTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!lease.get()) {
                    return;
                }
                logger.debug("{} Slave Node start heartbeat...", round);
                LeaseResult leaseResult = communications.lease(round, master, ownInstanceId);
                if (leaseResult == null ||
                        leaseResult.getRoundComparison() != 0) {
                    // 续租失败
                    if (lastTime.get() == -1) {
                        lastTime.set(DateTimeUtils.getCurrentTime());
                    }
                    if (lastTime.get() + protectedLeaseTime <= DateTimeUtils.getCurrentTime()) {
                        lease.set(false);
                    }
                } else {
                    lastTime.set(-1);
                }
            } catch (Exception e) {
                logger.error("{} Slave Node#FollowerOfLeaseTask error", round, e);
            } finally {
                if (lease.get()) {
                    // 还是从节点
                    scheduled.schedule(this, heartbeatTime, TimeUnit.MILLISECONDS);
                } else {
                    callback.accept(round);
                }
            }
        }
    }

}
