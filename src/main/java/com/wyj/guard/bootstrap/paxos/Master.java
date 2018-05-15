package com.wyj.guard.bootstrap.paxos;

import com.wyj.guard.share.Pair;
import com.wyj.guard.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// 主节点
public class Master extends Node implements Lease {

    private Logger logger = LoggerFactory.getLogger(Master.class);

    // 排除自身实例的其他所有实例的租约
    private List<Pair<String, Long>> leaseInstances;

    public Master(long round, String ownInstanceId, Consumer<Long> callback, List<String> nodes) {
        super(round, ownInstanceId, callback);
        long time = DateTimeUtils.getCurrentTime();
        leaseInstances = nodes.stream()
                .map(instanceId -> Pair.newPair(instanceId, time))
                .collect(Collectors.toList());
        scheduled.schedule(new LeaderOfLeaseTask(), 0, TimeUnit.MILLISECONDS);
    }

    public Master(long round, String ownInstanceId, List<String> nodes) {
        super(round, ownInstanceId, null);
        long time = DateTimeUtils.getCurrentTime();
        leaseInstances = nodes.stream()
                .map(instanceId -> Pair.newPair(instanceId, time))
                .collect(Collectors.toList());
    }


    @Override
    public LeaseResult lease(Long round, String instanceId) {
        if (!lease.get()) {
            return null;
        }
        logger.debug("round : {}, instanceId : {}", round, instanceId);
        synchronized (leaseInstances) {
            if (leaseInstances.removeIf(pair -> pair.getFirst().equals(instanceId))) {
                leaseInstances.add(Pair.newPair(instanceId, DateTimeUtils.getCurrentTime()));
            }
        }
        return null;
    }

    // 主节点维护租约的任务
    private class LeaderOfLeaseTask implements Runnable {
        @Override
        public void run() {
            try {
                if (!lease.get()) {
                    return;
                }
                logger.debug("{} Master Node start heartbeat...", round);
                long curTime = DateTimeUtils.getCurrentTime();
                synchronized (leaseInstances) {
                    // 已经断开连接的节点个数
                    int count = 0;
                    for (Pair<String, Long> pair : leaseInstances) {
                        if (pair.getSecond() + protectedLeaseTime <= curTime) {
                            count++;
                        }
                        logger.debug("instanceId : {}, time : {}", pair.getFirst(), pair.getSecond());
                    }
                    if (count > leaseInstances.size() / 2) {
                        lease.set(false);
                    }
                    logger.debug("{} Master Node heartbeat结束。count : {}, lease : {}", round, count, lease.get());
                }
            } catch (Exception e) {
                logger.error("{} Master Node#LeaderOfLeaseTask error", round, e);
            } finally {
                if (lease.get()) {
                    // 还是主节点
                    scheduled.schedule(this, heartbeatTime, TimeUnit.MILLISECONDS);
                } else {
                    callback.accept(round);
                }
            }
        }
    }
}
