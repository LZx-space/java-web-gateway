package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

/**
 * @author LZx
 * @since 2021/12/14
 */
@FunctionalInterface
public interface AuthorizationEvaluator extends BiFunction<Mono<Authentication>, AuthorizationContext, Mono<Boolean>> {

}
