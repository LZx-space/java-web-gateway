package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.evaluator;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.AuthorizationEvaluator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/15
 */
@Component
@Order(0)
public class PermitAllAuthorizationEvaluator implements AuthorizationEvaluator {

    private final ServerWebExchangeMatcher serverWebExchangeMatcher;

    public PermitAllAuthorizationEvaluator(@Qualifier("m1") ServerWebExchangeMatcher serverWebExchangeMatcher) {
        this.serverWebExchangeMatcher = serverWebExchangeMatcher;
    }

    @Override
    public Mono<Boolean> apply(Mono<Authentication> authenticationMono, AuthorizationContext context) {
        return serverWebExchangeMatcher.matches(context.getExchange())
                .map(ServerWebExchangeMatcher.MatchResult::isMatch);
    }

}
