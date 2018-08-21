package com.elfop.webflux.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.elfop.webflux.domain.AcAssociated;
import com.elfop.webflux.config.SystemUser;
import com.elfop.webflux.domain.TokenBind;
import com.elfop.webflux.exceptions.RedisException;
import com.elfop.webflux.util.MD5Util;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;

import static com.elfop.webflux.common.Dic.*;
import static com.elfop.webflux.common.KeyStorage.*;

/**
 * @Description: 对于获取用户token 先查 差不到 则创建   获取到token时 进行刷新 重新创建token
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-09 16:08
 */
@Component
public class RedisToken implements Token {

    @Resource
    private ReactiveHashOperations<String, String, String> reactiveHashOperations;


    public RedisToken() {
    }

    /**
     * 系统用户申请token
     *
     * @param userMono
     * @return
     */
    public Mono<String> apply(final Mono<SystemUser> userMono) {
        return userMono.transform(this::getToken);
    }

    private Mono<String> refresh(final Mono<String> tokenMono) {
        return tokenMono.transform(this::refreshToken);
    }

    /**
     * 验证token
     */
    public Mono<String> verify(final Mono<String> tokenMono) {
        return tokenMono.transform(this::getTokenState).transform(this::through).switchIfEmpty(Mono.just(AUTHENTICATED_USER_NONENTITY));
    }

    /**
     * 两个系统用户开始绑定
     */
    public Mono<String> bind(final Mono<TokenBind> tokenBindMono) {
        return tokenBindMono.transform(this::bindToken);
    }

    /**
     * 通过系统id 及 系统下userId 找到token
     *
     * @param userMono
     * @return
     */
    private Mono<String> getToken(final Mono<SystemUser> userMono) {
        return userMono.flatMap(
                user -> findToken(Mono.just(user)).transform(this::refresh)
                        .switchIfEmpty(
                                buildToken(Mono.just(user))
                        )
        );
    }

    /**
     * 接收用户的 token 并且刷新用户信息 返回新的token
     *
     * @param tokenMono
     * @return
     */
    private Mono<String> refreshToken(final Mono<String> tokenMono) {
        return tokenMono.transform(this::getAcAssociated).transform(this::refreshExecute);
    }

    /**
     * 获取该token下的所有关联关系
     *
     * @param tokenMono
     * @return
     */
    private Mono<AcAssociated> getAcAssociated(final Mono<String> tokenMono) {
        return tokenMono.map(token ->
                new AcAssociated(token
                        , reactiveHashOperations.entries(AUTHENTICATED_TOKEN_ASSOCIATED.concat(token))
                        .map(entry -> new SystemUser(entry.getValue(), entry.getKey()))
                )
        );
    }

    /**
     * 判断token状态是否通过
     *
     * @param stateMono
     * @return
     */
    private Mono<String> through(final Mono<String> stateMono) {
        return stateMono.flatMap(state -> {
            if (BLACK_STATE_UN_DISABLE.equals(state)) {
                return Mono.just(AUTHENTICATED_USER_THROUGH);
            } else {
                return Mono.just(AUTHENTICATED_USER_DISABLE);
            }
        });
    }

    /**
     * 通过hash结构 查找token
     *
     * @param userMono
     * @return
     */
    private Mono<String> findToken(final Mono<SystemUser> userMono) {
        return userMono.flatMap(user -> reactiveHashOperations.get(SYS_HASH_KEY.concat(user.getSysId()).concat(user.getId()), SYS_HASH_USER_TOKEN));
    }

    /**
     * 获取注册中心的token状态
     *
     * @param tokenMono
     * @return
     */
    private Mono<String> getTokenState(final Mono<String> tokenMono) {
        return tokenMono.flatMap(token -> reactiveHashOperations.get(AUTHENTICATED_USER_TOKEN + token, AUTHENTICATED_USER_STATE));
    }

    /**
     * 创建 hash 结构 token
     * 加密后的token :
     *
     * @param userMono
     * @return
     */
    private Mono<String> buildToken(final Mono<SystemUser> userMono) {
        return userMono.flatMap(user -> {
            String salt = String.valueOf(RandomUtil.randomInt(50000));
            String token = MD5Util.encrypt(user.getSysId().concat(user.getId()).concat(salt));

            Flux<Boolean> redis = saveTokenRedis(salt, token, user);

            Mono<Long> count = redis.filter(flag -> !flag).count();

            return count.flatMap(c -> {
                if (c > 0) {
                    return Mono.error(new RedisException("redis buildToken ERROR user : " + JSON.toJSONString(user)));
                } else {
                    return Mono.just(token);
                }
            });

        });
    }

    private Mono<String> bindToken(final Mono<TokenBind> tokenBindMono) {
        //原token 及 原系统用户 不需要改变 仅需挂载新的系统用户即可
        return tokenBindMono.flatMap(bind -> {

            Flux<Boolean> redis = bindTokenRedis(bind.getToken(), new SystemUser(bind.getId(), bind.getSysId()));
            Mono<Long> count = redis.filter(flag -> !flag).count();

            return count.flatMap(c -> {
                if (c > 0) {
                    return Mono.error(new RedisException("redis bindToken ERROR user : " + JSON.toJSONString(bind)));
                } else {
                    return Mono.just(SUCCESS);
                }
            });

        });

    }

    /**
     * @param acAssociated
     * @return
     */
    private Mono<String> refreshExecute(final Mono<AcAssociated> acAssociated) {
        return acAssociated.flatMap(ac -> ac.getSystemUsers().map(s -> s.getId() + s.getSysId()).reduce("", String::concat)
                .flatMap(unch -> {
                    final String salt = String.valueOf(RandomUtil.randomInt(50000));
                    final String token = MD5Util.encrypt(unch.concat(salt));

                    Flux<Boolean> redis = refreshTokenRedis(token, salt, ac);
                    Mono<Long> count = redis.filter(flag -> !flag).count();

                    return count.flatMap(c -> {
                        if (c > 0) {
                            return Mono.error(new RedisException("redis refreshTokenRedis ERROR user : " + JSON.toJSONString(ac)));
                        } else {
                            return Mono.just(token);
                        }
                    });

                }));

    }

    private Flux<Boolean> refreshTokenRedis(final String token, final String salt, final AcAssociated associated) {
        /*
         *删除原有数据
         * 删除认证中心token
         * 删除认证中心关联关系
         *更新系统用户的token
         *创建新的系统关联关系
         *创建新的token
         */
        Mono<Boolean> delAcTokenFlag = delAcToken(associated.getToken());
        Mono<Boolean> delAssociatedFlag = delAssociated(associated.getToken());
        Mono<Boolean> saveAcTokenFlag = saveAcToken(salt, token, BLACK_STATE_UN_DISABLE);
        Flux<Boolean> saveAssociatedFlag = associated.getSystemUsers().flatMap(user -> saveAssociated(token, user));
        Flux<Boolean> userFlag = associated.getSystemUsers().flatMap(user -> updateSysUser(token, user));
        return userFlag.concatWith(saveAssociatedFlag).concatWith(delAcTokenFlag).concatWith(delAssociatedFlag).concatWith(saveAcTokenFlag);
    }

    private Flux<Boolean> bindTokenRedis(final String token, final SystemUser user) {
        Mono<Boolean> sysUserFlag = saveSysUser(token, BLACK_STATE_UN_DISABLE, user);
        Mono<Boolean> associatedFlag = saveAssociated(token, user);
        return sysUserFlag.concatWith(associatedFlag);
    }

    private Flux<Boolean> saveTokenRedis(final String salt, final String token, final SystemUser user) {
        Mono<Boolean> acTokenFlag = saveAcToken(salt, token, BLACK_STATE_UN_DISABLE);
        Mono<Boolean> sysUserFlag = saveSysUser(token, BLACK_STATE_UN_DISABLE, user);
        Mono<Boolean> associatedFlag = saveAssociated(token, user);
        return acTokenFlag.concatWith(sysUserFlag).concatWith(associatedFlag);
    }

    private Mono<Boolean> delAcToken(final String token) {
        return reactiveHashOperations.delete(AUTHENTICATED_USER_TOKEN + token);
    }

    private Mono<Boolean> saveAcToken(final String salt, final String token, final String state) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(AUTHENTICATED_USER_SALT, salt);
        map.put(AUTHENTICATED_USER_STATE, state);
        return reactiveHashOperations.putAll(AUTHENTICATED_USER_TOKEN + token, map);
    }

    private Mono<Boolean> updateSysUser(final String token, final SystemUser user) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(SYS_HASH_USER_TOKEN, token);
        return reactiveHashOperations.putAll(SYS_HASH_KEY.concat(user.getSysId()).concat(user.getId()), map);
    }

    private Mono<Boolean> saveSysUser(final String token, final String state, final SystemUser user) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(SYS_HASH_USER_TOKEN, token);
        map.put(SYS_HASH_USER_STATE, state);
        return reactiveHashOperations.putAll(SYS_HASH_KEY.concat(user.getSysId()).concat(user.getId()), map);
    }

    private Mono<Boolean> delAssociated(final String token) {
        return reactiveHashOperations.delete(AUTHENTICATED_TOKEN_ASSOCIATED.concat(token));
    }

    private Mono<Boolean> saveAssociated(final String token, final SystemUser user) {
        return reactiveHashOperations.put(AUTHENTICATED_TOKEN_ASSOCIATED.concat(token), user.getSysId(), user.getId());
    }


}
