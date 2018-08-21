package com.elfop.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-09 19:14
 */
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveHashOperations<?,?,?> hashOperations(ReactiveRedisTemplate reactiveRedisTemplate){
        return reactiveRedisTemplate.opsForHash();
    }

}
