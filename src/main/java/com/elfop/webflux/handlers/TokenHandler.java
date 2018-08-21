package com.elfop.webflux.handlers;

import com.elfop.webflux.config.SystemUser;
import com.elfop.webflux.domain.TokenBind;
import com.elfop.webflux.service.RedisToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-09 17:40
 */
@Component
public class TokenHandler {

    private final RedisToken redisToken;

    public TokenHandler(RedisToken redisToken) {
        Assert.notNull(redisToken, "redisToken cannot be null");
        this.redisToken = redisToken;
    }

    public Mono<ServerResponse> apply(ServerRequest request) {
        return request.bodyToMono(SystemUser.class)
                .map(SystemUser::getSystemUser)
                .transform(redisToken::apply)
                .transform(this::stringResponse);
    }

    public Mono<ServerResponse> verify(ServerRequest request) {
        return request.bodyToMono(String.class).transform(redisToken::verify).transform(this::stringResponse);
    }

    public Mono<ServerResponse> bind(ServerRequest request){
        return request.bodyToMono(TokenBind.class)
                .map(TokenBind::getTokenBind)
                .transform(redisToken::bind)
                .transform(this::stringResponse);
    }

    private Mono<ServerResponse> stringResponse(Mono<String> locationResponseMono) {
        return locationResponseMono.flatMap(
                locationResponse -> ServerResponse.ok()
                        .contentType(TEXT_PLAIN)
                        .body(Mono.just(locationResponse), String.class)
        );
    }

}
