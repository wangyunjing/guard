package com.wyj.guard.bootstrap.paxos;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PaxosCommunications {

    private Logger logger = LoggerFactory.getLogger(PaxosCommunications.class);

    public static final String SCHEMA = "http";
    public static final String FIRST_PATH = "/paxos/prepare";
    public static final String SECOND_PATH = "/paxos/accept";
    public static final String LEASE_PATH = "/paxos/lease";

    private RestTemplate restTemplate;

    // 所有实例 - instanceId
    private List<String> allInstances;

    public PaxosCommunications(RestTemplate restTemplate,
                               List<String> allInstances) {
        this.restTemplate = restTemplate;
        this.allInstances = allInstances;
    }

    public int getInstanceNum() {
        return allInstances.size();
    }

    public List<VotingResult> first(Vote vote) {
        List<CompletableFuture<VotingResult>> completableFutures = allInstances.stream()
                .map(instanceId ->
                        CompletableFuture.supplyAsync(() -> {
                            String url = SCHEMA + "://" + instanceId + FIRST_PATH;
                            JSONObject jsonObject = putJSON(url, JSONObject.toJSONString(vote), null);
                            return jsonObject.toJavaObject(VotingResult.class);
                        }).exceptionally(throwable -> null)
                ).collect(Collectors.toList());
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .join();
        return completableFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<VotingResult> second(Vote vote) {
        List<CompletableFuture<VotingResult>> completableFutures = allInstances.stream()
                .map(instanceId ->
                        CompletableFuture.supplyAsync(() -> {
                            String url = SCHEMA + "://" + instanceId + SECOND_PATH;
                            JSONObject jsonObject = putJSON(url, JSONObject.toJSONString(vote), null);
                            if (jsonObject == null) {
                                return null;
                            }
                            return jsonObject.toJavaObject(VotingResult.class);
                        }).exceptionally(throwable -> null)
                ).collect(Collectors.toList());
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]))
                .join();
        return completableFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public LeaseResult lease(Long round, String master, String slave) {
        // TODO: 2018/5/13
        String url = SCHEMA + "://" + master + LEASE_PATH;
        Map<String, Object> map = new HashMap<>();
        map.put("round", round);
        map.put("instanceId", slave);
        try {
            JSONObject jsonObject = putJSON(url, "", map);
            if (jsonObject == null) {
                return null;
            }
            return jsonObject.toJavaObject(LeaseResult.class);
        } catch (Exception e) {
            //
        }
        return null;
    }

    public void setAllInstances(List<String> allInstances) {
        this.allInstances = allInstances;
    }

    public List<String> getAllInstances() {
        return allInstances;
    }

    private JSONObject putJSON(String url, String body, Map<String, Object> map) {
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
                }
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(uriBuilder.build(),
                    HttpMethod.PUT, httpEntity, JSONObject.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            throw new RuntimeException("请求失败");
        } catch (Exception e) {
            logger.error("请求失败! URL:{}, body:{}, requestParam:{}", url, body, map, e);
            throw new RuntimeException("请求失败");
        }
    }
}
