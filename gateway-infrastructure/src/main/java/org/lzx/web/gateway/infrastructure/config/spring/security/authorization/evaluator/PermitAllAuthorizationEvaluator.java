package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.AuthorizationEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/15
 */
@Slf4j
public class PermitAllAuthorizationEvaluator implements AuthorizationEvaluator {

    private final ServerWebExchangeMatcher serverWebExchangeMatcher;

    public PermitAllAuthorizationEvaluator(ServerWebExchangeMatcher serverWebExchangeMatcher) {
        log.info("新建权限评估器：[{}]\n", this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        this.serverWebExchangeMatcher = serverWebExchangeMatcher;
    }

    @Override
    public Mono<Boolean> apply(Mono<Authentication> authenticationMono, AuthorizationContext context) {
        return serverWebExchangeMatcher.matches(context.getExchange())
                .map(ServerWebExchangeMatcher.MatchResult::isMatch);
    }

}
