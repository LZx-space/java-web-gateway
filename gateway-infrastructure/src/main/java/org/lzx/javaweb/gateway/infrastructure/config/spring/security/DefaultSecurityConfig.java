package org.lzx.javaweb.gateway.infrastructure.config.spring.security;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenUpStreamFilter;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.response.HeaderWritableResponseFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.autoconfigure.security.reactive.StaticResourceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * Web Security配置
 *
 * @author LZx
 * @since 2021/9/26
 */
@EnableWebFluxSecurity
public class DefaultSecurityConfig {

    @Value("${spring.cloud.gateway.security.login-page-url:http://localhost/login.html}")
    private String loginPageUrl;

    private static final String LOGIN_PROCESSING_URL = "/login";

    private final ReactiveAuthorizationManager<AuthorizationContext> authorizationManager;

    private final ServerAuthenticationSuccessHandler authenticationSuccessHandler;

    private final ServerAuthenticationFailureHandler authenticationFailureHandler;

    public DefaultSecurityConfig(
            ReactiveAuthorizationManager<AuthorizationContext> authorizationManager,
            ServerAuthenticationSuccessHandler authenticationSuccessHandler,
            ServerAuthenticationFailureHandler authenticationFailureHandler) {
        this.authorizationManager = authorizationManager;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        ServerWebExchangeMatcher loginWebExchangeMatcher = ServerWebExchangeMatchers
                .pathMatchers(HttpMethod.POST, LOGIN_PROCESSING_URL);
        return http
                .securityMatcher(securityWebExchangeMatcher())
                .addFilterBefore(new AuthenticationTokenUpStreamFilter(),
                        SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION
                )
                .addFilterBefore(new HeaderWritableResponseFilter(),
                        SecurityWebFiltersOrder.EXCEPTION_TRANSLATION
                )
                .authorizeExchange(exchanges -> exchanges
                        .matchers(loginWebExchangeMatcher).permitAll()
                        .anyExchange().access(authorizationManager)
                )
                .formLogin(formLoginSpec -> formLoginSpec
                        .loginPage(loginPageUrl)
                        .requiresAuthenticationMatcher(loginWebExchangeMatcher)
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler)
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(Customizer.withDefaults()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    /**
     * 需要安全处理的请求的匹配器
     *
     * @return 安全处理的请求的匹配器
     */
    private ServerWebExchangeMatcher securityWebExchangeMatcher() {
        StaticResourceRequest.StaticResourceServerWebExchange staticResourceServerWebExchange = PathRequest
                .toStaticResources()
                .atCommonLocations();
        return new NegatedServerWebExchangeMatcher(staticResourceServerWebExchange);
    }

}
