package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.converter.UsernamePasswordTokenConverter;
import org.lzx.javaweb.gateway.infrastructure.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * 将认证信息从请求头传递到上游的过滤器
 *
 * @author LZx
 * @since 2021/9/26
 */
@Slf4j
public class AuthenticationTokenUpStreamFilter implements GlobalFilter, Ordered {

    private static final String ROUTED_ATTRIBUTE_NAME = "authentication_token_up_stream_filter_routed";

    /**
     * 下游服务认证信息请求头的名字
     */
    private static final String DOWN_STREAM_AUTHENTICATION_HEADER_NAME = "X-Authentication";

    private final AuthenticationTrustResolver authTrustResolver = new AuthenticationTrustResolverImpl();

    private final AuthenticationTokenConverter authenticationTokenConverter = new UsernamePasswordTokenConverter();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isAlreadyRouted(exchange)) {
            removeAlreadyRouted(exchange);
            return chain.filter(exchange);
        }
        setAlreadyRouted(exchange);
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

    @Override
    public int getOrder() {
        return NettyRoutingFilter.ORDER - 1;
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
        Map<String, Object> details = authenticationTokenConverter.convert(authentication);
        String write;
        try {
            write = JsonUtils.write(details);
        } catch (JsonProcessingException e) {
            log.error("序列化认证信息失败", e);
            return rawRequest;
        }
        String token = Base64.getEncoder().encodeToString(write.getBytes(StandardCharsets.UTF_8));
        return rawRequest.mutate()
                .header(DOWN_STREAM_AUTHENTICATION_HEADER_NAME, token)
                .build();
    }

    private boolean isAlreadyRouted(ServerWebExchange exchange) {
        return (boolean) exchange.getAttributes().getOrDefault(ROUTED_ATTRIBUTE_NAME, false);
    }

    private void setAlreadyRouted(ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ROUTED_ATTRIBUTE_NAME, true);
    }

    private void removeAlreadyRouted(ServerWebExchange exchange) {
        exchange.getAttributes().remove(ROUTED_ATTRIBUTE_NAME);
    }

}
