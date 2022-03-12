package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

/**
 * 基于认证数据中授权API属性的授权管理器
 *
 * @author LZx
 * @since 2021/12/14
 */
@Slf4j
public class ApiAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .filter(AuthorizationManagerHelper::isNotAnonymous)
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .ofType(ApiGrantedAuthority.class)
                .flatMapIterable(ApiGrantedAuthority::getPermittedApis)
                .flatMap(permittedApi -> {
                    HttpMethod method = permittedApi.getMethod();
                    String antPathUrl = permittedApi.getAntPathUrl();
                    return ServerWebExchangeMatchers.pathMatchers(method, antPathUrl).matches(context.getExchange());
                })
                .any(ServerWebExchangeMatcher.MatchResult::isMatch)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
