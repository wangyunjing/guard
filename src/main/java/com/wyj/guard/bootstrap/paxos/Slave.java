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

    public Slave(long round, Consumer<Long> callback, String master,
                 PaxosCommunications communications) {
        super(round, callback);
        this.communications = communications;
        this.master = master;
        scheduled.schedule(new FollowerOfLeaseTask(), 0, TimeUnit.MILLISECONDS);
    }

    // 从节点维护租约的任务
    private class FollowerOfLeaseTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!lease.get()) {
                    return;
                }
                LeaseResult leaseResult = communications.lease(round, master);
                if (leaseResult == null ||
                        leaseResult.getRoundComparison() != 0) {
                    if (lastTime.get() != -1 &&
                            lastTime.get() + protectedLeaseTime <= DateTimeUtils.getCurrentTime()) {
                        lease.set(false);
                    } else {
                        lastTime.set(DateTimeUtils.getCurrentTime());
                    }
                }
            } catch (Exception e) {
                logger.error("FollowerOfLeaseTask error", e);
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
