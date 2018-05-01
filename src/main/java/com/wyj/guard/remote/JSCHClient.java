
package com.wyj.guard.remote;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.share.enums.InstanceStatus;
import com.wyj.guard.utils.StringPlaceholderResolver;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * JSCH的实现 {@link SSHClient}
 */
public class JSCHClient implements SSHClient {

    private final Logger logger = LoggerFactory.getLogger(JSCHClient.class);

    // 用于获取环境变量
    private static final String DEFAULT_COMMAND = "source /etc/profile;source ~/.bash_profile;";

    // 判断实例是否启动
    private static final String PROCESS_ID = "netstat -tunlp|grep $|awk '{print $7}'";

    // 连接的默认端口号
    private static final Integer DEFAULT_PORT = 22;

    private RestTemplate restTemplate;

    public JSCHClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean startInstance(InstanceInfo instanceInfo) {
        boolean exec = commandExec(instanceInfo, openChannel ->
                        openChannel.setCommand(DEFAULT_COMMAND + instanceInfo.getStartCommand()),
                instanceInfo.getStartCommand());
        if (exec) {
            logger.info("启动实例{}成功!", instanceInfo.getInstanceId());
        } else {
            logger.warn("启动实例{}失败!", instanceInfo.getInstanceId());
        }
        return exec;
    }

    @Override
    public boolean closeInstance(InstanceInfo instanceInfo) {
        String processCommand = StringPlaceholderResolver.resolvePlaceholder(PROCESS_ID,
                String.valueOf(instanceInfo.getPort()));

        String[] strings = commandExecHasResults(instanceInfo, (channel) ->
                channel.setCommand(DEFAULT_COMMAND + processCommand), processCommand);
        if (strings == null) {
            logger.warn("关闭实例{}失败!", instanceInfo.getInstanceId());
            return false;
        }
        if (strings.length == 0) {
            logger.info("关闭实例{}成功!", instanceInfo.getInstanceId());
            return true;
        }
        String processId = strings[0].split("/")[0];

        boolean exec = commandExec(instanceInfo, (channel) ->
                        channel.setCommand(DEFAULT_COMMAND + "kill -9 " + processId),
                "kill -9 " + processId);
        if (exec) {
            logger.info("关闭实例{}成功!", instanceInfo.getInstanceId());
        } else {
            logger.warn("关闭实例{}失败!", instanceInfo.getInstanceId());
        }
        return exec;
    }

    @Override
    public InstanceStatus getInstanceStatus(InstanceInfo instanceInfo) {
        Session session = getSession(instanceInfo);
        if (session == null) {
            logger.info("实例{}的状态为{}", instanceInfo.getInstanceId(), InstanceStatus.SERVER_DOWN);
            return InstanceStatus.SERVER_DOWN;
        }
        try {
            String processCommand = StringPlaceholderResolver.resolvePlaceholder(PROCESS_ID,
                    String.valueOf(instanceInfo.getPort()));

            String[] strings = commandExecHasResults(session, (channel) ->
                    channel.setCommand(DEFAULT_COMMAND + processCommand), processCommand, instanceInfo);
            if (strings == null || strings.length == 0) {
                logger.info("实例{}的状态为{}!", instanceInfo.getInstanceId(), InstanceStatus.SHUTDOWN);
                return InstanceStatus.SHUTDOWN;
            }
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        try {
            URI uri = new URIBuilder().setScheme("http")
                    .setHost(instanceInfo.getIp())
                    .setPort(instanceInfo.getPort())
                    .setPath(instanceInfo.getHealthUrl())
                    .build();
            ResponseEntity<JSONObject> entity = restTemplate.getForEntity(uri, JSONObject.class);
            if (!entity.getStatusCode().is2xxSuccessful()) {
                logger.warn("statusCode : {}, body={}", entity.getStatusCodeValue(),
                        entity.getBody().toJSONString());
                throw new RuntimeException("访问错误!");
            }
        } catch (Exception e) {
            logger.warn("实例{}的状态为{}!", instanceInfo.getInstanceId(), InstanceStatus.DOWN, e);
            return InstanceStatus.DOWN;
        }
        return InstanceStatus.UP;
    }

    // 执行命令
    private boolean commandExec(InstanceInfo instanceInfo,
                                SSHCommandExec sshCommandExec,
                                String command) {

        Session session = getSession(instanceInfo);
        try {
            return commandExec(session, sshCommandExec, command, instanceInfo);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private boolean commandExec(Session session,
                                SSHCommandExec sshCommandExec,
                                String command, InstanceInfo instanceInfo) {
        if (session == null) {
            return false;
        }
        ChannelExec execChannel = null;
        try {
            execChannel = (ChannelExec) session.openChannel("exec");
            if (sshCommandExec != null) {
                sshCommandExec.exec(execChannel);
            }
            execChannel.connect();
        } catch (Exception e) {
            logger.warn("执行{}失败！instanceId : {}", command, instanceInfo.getInstanceId(), e);
            return false;
        } finally {
            if (execChannel != null && !execChannel.isClosed()) {
                execChannel.disconnect();
            }
        }
        return true;
    }


    private String[] commandExecHasResults(InstanceInfo instanceInfo,
                                           SSHCommandExec sshCommandExec,
                                           String command) {
        Session session = getSession(instanceInfo);
        try {
            return commandExecHasResults(session, sshCommandExec, command, instanceInfo);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private String[] commandExecHasResults(Session session,
                                           SSHCommandExec sshCommandExec,
                                           String command, InstanceInfo instanceInfo) {
        if (session == null) {
            return null;
        }
        String[] results = null;
        BufferedReader bufferedReader = null;
        ChannelExec execChannel = null;
        try {
            if (sshCommandExec != null) {
                sshCommandExec.exec(execChannel);
            }
            execChannel.connect();

            List<String> resultList = new ArrayList<>();
            String result = null;
            bufferedReader = new BufferedReader(new InputStreamReader(execChannel.getInputStream(),
                    "UTF-8"));
            while ((result = bufferedReader.readLine()) != null) {
                resultList.add(result);
            }
            results = resultList.toArray(new String[resultList.size()]);
        } catch (Exception e) {
            logger.warn("执行{}失败！instanceId : {}", command, instanceInfo.getInstanceId(), e);
            results = null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    //
                }
            }
            if (execChannel != null && !execChannel.isClosed()) {
                execChannel.disconnect();
            }
        }
        return results;
    }

    private Session getSession(InstanceInfo instanceInfo) {
        String ip = instanceInfo.getIp();
        String username = instanceInfo.getUsername();
        String password = instanceInfo.getPassword();

        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, ip, DEFAULT_PORT);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
            session.setTimeout(30000);
            session.connect();
        } catch (Exception e) {
            logger.warn("获取Session失败！instanceId : {}", instanceInfo.getInstanceId(), e);
            return null;
        }
        return session;
    }

    interface SSHCommandExec {
        void exec(ChannelExec openChannel);
    }

}

