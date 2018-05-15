package com.wyj.guard.bootstrap.paxos;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.wyj.guard.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class Node {
    private Logger logger = LoggerFactory.getLogger(Node.class);

    // 回合数
    protected final long round;

    // 自身的实例ID
    protected final String ownInstanceId;

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

    public Node(long round, String ownInstanceId, Consumer<Long> callback) {
        this.round = round;
        this.ownInstanceId = ownInstanceId;
        this.callback = callback;
    }

    public void destroy() {
        logger.debug("node destroy...");
        lease.set(false);
        ThreadPoolUtils.shutdown(scheduled);
        scheduled = null;
        logger.debug("node destroy成功");
    }

    public long getRound() {
        return round;
    }
}
