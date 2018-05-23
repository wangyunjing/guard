package com.wyj.guard.bootstrap;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.wyj.guard.bootstrap.paxos.*;
import com.wyj.guard.context.DefaultGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.GuardProperties;
import com.wyj.guard.info.ApplicationManager;
import com.wyj.guard.utils.InetUtils;
import com.wyj.guard.web.InstanceCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CloudLauncher extends AbstractLauncher implements Acceptor, Lease {

    private Logger logger = LoggerFactory.getLogger(CloudLauncher.class);

    private GuardProperties guardProperties;

    private Paxos paxos;

    private ApplicationManager cloudManager;

    private SingleLauncher launcher;

    // 用来重新选择主节点的线程池
    private ExecutorService task;

    public CloudLauncher(GuardContext guardContext) {
        super(guardContext);
        guardProperties = guardContext.getGuardProperties();
        task = Executors.newSingleThreadExecutor(new DaemonThreadFactory("CloudLauncher-Task"));
    }

    public Object isMaster(String instanceId) {
        return paxos.isMaster(instanceId);
    }

    @Override
    public synchronized boolean launch() {
        if (launcher != null) {
            launcher.destroy();
            launcher = null;
        }
        // 重新加载应用
        loadApplications();
        // 投票 - 选举主节点
        if (paxos == null) {
            if (guardContext instanceof DefaultGuardContext) {
                DefaultGuardContext defaultGuardContext = (DefaultGuardContext) guardContext;
                paxos = new Paxos(defaultGuardContext.getRestTemplate(),
                        guardProperties.getPaxosInstanceNum(),
                        getOwnInstanceId(), getAllInstanceIds(),
                        () -> CompletableFuture.supplyAsync(this::launch, task));
            } else {
                throw new RuntimeException("没有找到RestTemplate。");
            }
        } else {
            paxos.setAllInstances(getAllInstanceIds());
        }
        if (paxos.propose().equals(PaxosStatus.LEADER)) {
            // 自身为主节点
            launcher = new SingleLauncher(guardContext, applicationManagers);
            return cloudManager.launch() && launcher.launch();
        }
        return true;
    }

    @Override
    protected void loadApplications() {
        super.loadApplications();
        if (cloudManager != null) {
            cloudManager.virtualClose();
            cloudManager.selfClose();
        }
        // 获取集群实例的应用管理器
        cloudManager = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(guardProperties.getClusterApplicationId()))
                .findFirst().get();
        // 移除集群实例的应用管理器
        applicationManagers.remove(cloudManager);
    }

    private String getOwnInstanceId() {
        List<String> instanceIds = getAllInstanceIds();
        String[] ips = InetUtils.allNotLoopbackIps();
        List<String> tmp = new ArrayList<>();
        tmp.addAll(Arrays.stream(ips).collect(Collectors.toList()));
        tmp.add("127.0.0.1");
        tmp.add("localhost");
        ips = tmp.toArray(new String[tmp.size()]);
        String port = guardContext.getEnvironment().getProperty("server.port");
        for (String ip : ips) {
            String instanceId = ip + ":" + port;
            if (instanceIds.contains(instanceId)) {
                return instanceId;
            }
        }
        throw new RuntimeException("没有找到自身实例ID.");
    }

    private List<String> getAllInstanceIds() {
        InstanceCondition condition = new InstanceCondition();
        condition.setApplicationId(guardProperties.getClusterApplicationId());
        return Arrays.stream(cloudManager.queryInstance(condition))
                .map(instanceInfo -> instanceInfo.getInstanceId())
                .collect(Collectors.toList());
    }

    @Override
    public VotingResult preparePhase(Vote vote) {
        return paxos.preparePhase(vote);
    }

    @Override
    public VotingResult acceptPhase(Vote vote) {
        return paxos.acceptPhase(vote);
    }

    @Override
    public LeaseResult lease(Long round, String instanceId) {
        return paxos.lease(round, instanceId);
    }
}
