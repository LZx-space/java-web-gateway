package org.lzx.javaweb.gateway.infrastructure.config.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 抽象该过滤器为了聚合所有有安全作用的过滤器在一起方便统一管理
 *
 * @author LZx
 * @since 2021/9/30
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InvocationFilter implements WebFilter {

    private final List<MatcherSecurityFilter> filters;

    public InvocationFilter(List<MatcherSecurityFilter> filters) {
        Assert.notNull(filters, "安全过滤器不能为null");
        this.filters = filters;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Flux.fromIterable(filters)
                .filterWhen(matcherSecurityFilter -> matcherSecurityFilter.matches(exchange))
                .next()
                .flatMap(f -> f.filter(exchange, chain))
                .then(chain.filter(exchange));
    }

}
