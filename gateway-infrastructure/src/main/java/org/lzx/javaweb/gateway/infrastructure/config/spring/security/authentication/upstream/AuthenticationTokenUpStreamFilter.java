package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 将认证信息从请求头传递到上游的过滤器
 *
 * @author LZx
 * @since 2021/9/26
 */
public class AuthenticationTokenUpStreamFilter implements WebFilter {

    /**
     * 下游服务认证信息请求头的名字
     */
    private static final String DOWN_STREAM_AUTHENTICATION_HEADER_NAME = "X-Authentication";

    private final AuthenticationTrustResolver authTrustResolver = new AuthenticationTrustResolverImpl();

    private final AuthenticationTokenConverter authenticationTokenConverter = new DefaultAuthenticationTokenConverter();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    Authentication authentication = ctx.getAuthentication();
                    if (!isAuthenticated(authentication)) {
                        return chain.filter(exchange);
                    }
                    ServerHttpRequest rawRequest = exchange.getRequest();
                    ServerHttpRequest authenticationHeaderRequest = authenticationHeaderRequest(rawRequest, authentication);
                    return chain.filter(exchange.mutate().request(authenticationHeaderRequest).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * 是否当前用户已认证
     *
     * @param authentication 认证信息
     * @return 已认证则true
     */
    private boolean isAuthenticated(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        if (authTrustResolver.isAnonymous(authentication)) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /**
     * 添加自定义认证信息头
     *
     * @param rawRequest 原请求
     * @return 修改后的请求，将传递到下游
     */
    private ServerHttpRequest authenticationHeaderRequest(ServerHttpRequest rawRequest, Authentication authentication) {
        String token = authenticationTokenConverter.convert(authentication);
        return rawRequest.mutate()
                .header(DOWN_STREAM_AUTHENTICATION_HEADER_NAME, token)
                .build();
    }

}
