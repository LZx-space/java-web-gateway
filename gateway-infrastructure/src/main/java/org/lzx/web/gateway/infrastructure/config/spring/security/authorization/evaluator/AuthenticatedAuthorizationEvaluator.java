package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.AuthorizationEvaluator;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/15
 */
@Slf4j
public class AuthenticatedAuthorizationEvaluator implements AuthorizationEvaluator {

    private final ServerWebExchangeMatcher serverWebExchangeMatcher;

    public AuthenticatedAuthorizationEvaluator(ServerWebExchangeMatcher serverWebExchangeMatcher) {
        log.info("新建权限评估器：[{}]\n", this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
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
