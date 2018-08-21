package com.elfop.webflux.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description: 对于用户的黑名单
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-10 14:26
 */
@Component
public class BlackList {

    @Resource
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

}
