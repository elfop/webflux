package com.elfop.webflux.routers;

import com.elfop.webflux.handlers.TokenHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-11 9:53
 */
@Configuration
public class TokenRouter {

    private static final String API_PATH = "/api/token";
    private static final String APPLY_PATH = "/apply";
    private static final String VERIFY_PATH = "/verify";
    private static final String BIND_PATH = "/bind";

    @Bean
    public RouterFunction<ServerResponse> routeApply(final TokenHandler handler) {
        return nest(path(API_PATH),
                nest(accept(APPLICATION_JSON),
                        route(POST(APPLY_PATH), handler::apply)
                ));
    }

    @Bean
    public RouterFunction<ServerResponse> verify(final TokenHandler handler) {
        return nest(path(API_PATH),
                nest(accept(TEXT_PLAIN),
                        route(POST(VERIFY_PATH), handler::verify)
                )
        );
    }

    @Bean
    public RouterFunction<ServerResponse> bind(final TokenHandler handler){
        return nest(path(API_PATH),
                nest(accept(APPLICATION_JSON),
                        route(POST(BIND_PATH), handler::bind)
                ));
    }

}
