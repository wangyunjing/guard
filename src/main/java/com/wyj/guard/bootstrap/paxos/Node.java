package com.wyj.guard.bootstrap.paxos;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.wyj.guard.utils.ThreadPoolUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class Node {

    // 回合数
    protected final long round;

    // 标志是否继续维护租约
    protected AtomicBoolean lease = new AtomicBoolean(true);

    // 回调函数
    protected Consumer<Long> callback;

    // 心跳时间
    protected Long heartbeatTime = 3000L;

    // 保护租约时间(防止网路波动)
    protected Long protectedLeaseTime = 8000L;

    // 调度器
    protected ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1,
            new DaemonThreadFactory("Node-Scheduled"));

    public Node(long round, Consumer<Long> callback) {
        this.round = round;
        this.callback = callback;
    }

    public void destroy() {
        lease.set(false);
        ThreadPoolUtils.shutdown(scheduled);
        scheduled = null;
    }

    public long getRound() {
        return round;
    }
}
