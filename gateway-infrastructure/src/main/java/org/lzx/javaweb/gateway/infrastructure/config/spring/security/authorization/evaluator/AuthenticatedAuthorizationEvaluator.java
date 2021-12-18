package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.evaluator;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.AuthorizationEvaluator;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.matcher.RefreshableMatcherDecorator;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
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
@Order(1)
public class AuthenticatedAuthorizationEvaluator implements AuthorizationEvaluator {

    private final RefreshableMatcherDecorator serverWebExchangeMatcher;

    public AuthenticatedAuthorizationEvaluator(@Qualifier("m2") RefreshableMatcherDecorator serverWebExchangeMatcher) {
        this.serverWebExchangeMatcher = serverWebExchangeMatcher;
    }

    @Override
    public Mono<Boolean> apply(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(e -> AuthorizationManagerHelper.isNotAnonymous(e) && e.isAuthenticated())
                .flatMap(e -> serverWebExchangeMatcher.matches(context.getExchange())
                        .map(ServerWebExchangeMatcher.MatchResult::isMatch)
                )
                .defaultIfEmpty(false);
    }

}
