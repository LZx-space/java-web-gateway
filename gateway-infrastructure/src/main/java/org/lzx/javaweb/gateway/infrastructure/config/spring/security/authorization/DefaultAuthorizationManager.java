package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author LZx
 * @since 2021/12/14
 */
@Slf4j
public class DefaultAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final List<AuthorizationEvaluator> authorizationEvaluators;

    public DefaultAuthorizationManager(List<AuthorizationEvaluator> authorizationEvaluators) {
        Assert.notEmpty(authorizationEvaluators, "授权评估器不能为空");
        this.authorizationEvaluators = authorizationEvaluators;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return Flux.fromIterable(this.authorizationEvaluators)
                .concatMap(evaluator -> evaluator.apply(authentication, context))
                .any(Boolean::booleanValue)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
