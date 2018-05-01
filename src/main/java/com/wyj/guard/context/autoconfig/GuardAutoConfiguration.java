package com.wyj.guard.context.autoconfig;

import com.wyj.guard.context.GuardProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({GuardProperties.class})
public class GuardAutoConfiguration {

    @Bean
    public RestTemplate httpRestTemplate(HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        return restTemplate;
    }

    @Bean
    HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(GuardProperties guardProperties) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(guardProperties.getConnectionRequestTimeout());
        httpRequestFactory.setConnectTimeout(guardProperties.getConnectTimeout());
        httpRequestFactory.setReadTimeout(guardProperties.getReadTimeout());
        return httpRequestFactory;
    }


//    @Autowired
//    private RedisProperties redisProperties;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
//        jedisPoolConfig.setMinIdle(redisProperties.getMinIdle());
//        jedisPoolConfig.setMaxIdle(redisProperties.getMaxIdle());
//        jedisPoolConfig.setMaxTotal(redisProperties.getMaxTotal());
//        jedisPoolConfig.setMaxWaitMillis(redisProperties.getMaxWaitMillis());
//
//        JedisConnectionFactory jedisConnectionFactory=new JedisConnectionFactory();
//        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
//        jedisConnectionFactory.setDatabase(redisProperties.getDb());
//        jedisConnectionFactory.setHostName(redisProperties.getHost());
//        jedisConnectionFactory.setPort(redisProperties.getPort());
//        if (redisProperties.getPassword() != null) {
//            jedisConnectionFactory.setPassword(redisProperties.getPassword());
//        }
//        return jedisConnectionFactory;
//    }
//
//    @Bean
//    public StringRedisTemplate redisTemplate(){
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
//        return stringRedisTemplate;
//    }
}
