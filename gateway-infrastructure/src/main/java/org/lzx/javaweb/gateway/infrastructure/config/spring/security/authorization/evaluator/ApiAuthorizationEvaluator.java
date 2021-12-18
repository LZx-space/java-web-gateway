package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.evaluator;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.ApiGrantedAuthority;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.AuthorizationEvaluator;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/15
 */
@Component
@Order(2)
public class ApiAuthorizationEvaluator implements AuthorizationEvaluator {

    @Override
    public Mono<Boolean> apply(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(AuthorizationManagerHelper::isNotAnonymous)
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .filter(authority -> authority instanceof ApiGrantedAuthority)
                .flatMapIterable(authority -> {
                    ApiGrantedAuthority apiGrantedAuthority = (ApiGrantedAuthority) authority;
                    return apiGrantedAuthority.getPermittedApis();
                })
                .flatMap(permittedApi -> {
                    HttpMethod method = permittedApi.getMethod();
                    String antPathUrl = permittedApi.getAntPathUrl();
                    return ServerWebExchangeMatchers.pathMatchers(method, antPathUrl).matches(context.getExchange());
                })
                .any(ServerWebExchangeMatcher.MatchResult::isMatch)
                .defaultIfEmpty(false);
    }

}
