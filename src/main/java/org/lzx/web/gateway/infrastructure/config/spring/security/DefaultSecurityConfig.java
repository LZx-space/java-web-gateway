package org.lzx.web.gateway.infrastructure.config.spring.security;

import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.ApiAuthorizationManager;
import org.lzx.web.gateway.infrastructure.config.spring.security.response.HeaderWritableResponseFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.boot.autoconfigure.security.reactive.StaticResourceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Web Security配置
 *
 * @author LZx
 * @since 2021/9/26
 */
@EnableWebFluxSecurity
public class DefaultSecurityConfig {

    @Value("${spring.cloud.gateway.security.login-page-url}")
    private String loginPageUrl;

    private static final String LOGIN_PROCESSING_URL = "/login";

    private static final String LOGOUT_PROCESSING_URL = "/logout";

    private final ReactiveAuthenticationManager authenticationManager;

    private final ServerWebExchangeMatcher denyAllExchangeMatcher;

    private final ServerWebExchangeMatcher permitAllExchangeMatcher;

    private final ServerWebExchangeMatcher authenticatedExchangeMatcher;

    private final ApiAuthorizationManager apiAuthorizationManager;

    private final ServerAuthenticationSuccessHandler authenticationSuccessHandler;

    private final ServerAuthenticationFailureHandler authenticationFailureHandler;

    private final ServerRequestCache serverRequestCache;

    public DefaultSecurityConfig(
            ReactiveAuthenticationManager authenticationManager,
            ServerWebExchangeMatcher denyAllExchangeMatcher,
            ServerWebExchangeMatcher permitAllExchangeMatcher,
            ServerWebExchangeMatcher authenticatedExchangeMatcher,
            ApiAuthorizationManager apiAuthorizationManager,
            ServerAuthenticationSuccessHandler authenticationSuccessHandler,
            ServerAuthenticationFailureHandler authenticationFailureHandler, ServerRequestCache serverRequestCache) {
        this.authenticationManager = authenticationManager;
        this.denyAllExchangeMatcher = denyAllExchangeMatcher;
        this.permitAllExchangeMatcher = permitAllExchangeMatcher;
        this.authenticatedExchangeMatcher = authenticatedExchangeMatcher;
        this.apiAuthorizationManager = apiAuthorizationManager;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.serverRequestCache = serverRequestCache;
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        ServerWebExchangeMatcher loginExchangeMatcher = ServerWebExchangeMatchers
                .pathMatchers(HttpMethod.POST, LOGIN_PROCESSING_URL);
        return http
                .securityMatcher(securityWebExchangeMatcher())
                .addFilterBefore(new HeaderWritableResponseFilter(), SecurityWebFiltersOrder.EXCEPTION_TRANSLATION)
                .authorizeExchange(exchanges -> exchanges
                        // 自定义的权限评估流程中（参见ReadMe）denyAll、permitAll、authenticated三个方法的顺序不能变
                        // DelegatingReactiveAuthorizationManager#check中为过滤所有匹配的验证器，使用匹配到的第一个用于评估权限
                        .matchers(denyAllExchangeMatcher).denyAll()
                        .matchers(loginExchangeMatcher, permitAllExchangeMatcher).permitAll()
                        .matchers(authenticatedExchangeMatcher).authenticated()
                        .anyExchange().access(apiAuthorizationManager)
                )
                .formLogin(formLoginSpec -> formLoginSpec
                        .loginPage(loginPageUrl)
                        .authenticationManager(authenticationManager)
                        .requiresAuthenticationMatcher(loginExchangeMatcher)
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler)
                )
                .logout(logoutSpec -> logoutSpec
                        // todo 按逻辑需要POST请求，同时也要做CSRF处理
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers(LOGOUT_PROCESSING_URL))
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(Customizer.withDefaults())
                )
                .requestCache(requestCacheSpec -> requestCacheSpec
                        .requestCache(serverRequestCache)
                )
                .cors(corsSpec -> corsSpec
                        .configurationSource(corsConfigurationSource())
                )
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

    /**
     * 跨域处理
     *
     * @return 跨域配置
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
