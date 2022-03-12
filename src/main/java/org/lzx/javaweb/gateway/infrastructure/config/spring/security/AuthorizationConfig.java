package org.lzx.javaweb.gateway.infrastructure.config.spring.security;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.ApiAuthorizationManager;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.AuthorizationProperties;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.HttpMethod;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/9/29
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AuthorizationProperties.class)
public class AuthorizationConfig {

    public static final String DENY_ALL_EXCHANGE_MATCHER_BEAN_NAME = "denyAllExchangeMatcher";

    public static final String PERMIT_ALL_EXCHANGE_MATCHER_BEAN_NAME = "permitAllExchangeMatcher";

    public static final String AUTHENTICATED_EXCHANGE_MATCHER_BEAN_NAME = "authenticatedExchangeMatcher";

    @RefreshScope
    @Bean(DENY_ALL_EXCHANGE_MATCHER_BEAN_NAME)
    ServerWebExchangeMatcher denyAllServerWebExchangeMatcher(AuthorizationProperties authorizationProperties) {
        log.info("创建拒绝任何外部访问的请求匹配器-[0]-开始");
        Map<HttpMethod, List<String>> denyAll = authorizationProperties.getDenyAll();
        OrServerWebExchangeMatcher denyAllMatcher = AuthorizationManagerHelper.orServerWebExchangeMatcher(denyAll);
        log.info("创建拒绝任何外部访问的请求匹配器-[1]-成功");
        return denyAllMatcher;
    }

    @RefreshScope
    @Bean(PERMIT_ALL_EXCHANGE_MATCHER_BEAN_NAME)
    ServerWebExchangeMatcher permitAllServerWebExchangeMatcher(AuthorizationProperties authorizationProperties) {
        log.info("创建任何人可访问的请求匹配器-[0]-开始");
        Map<HttpMethod, List<String>> permitAll = authorizationProperties.getPermitAll();
        ServerWebExchangeMatcher permitAllMatcher = AuthorizationManagerHelper.orServerWebExchangeMatcher(permitAll);
        log.info("创建任何人可访问的请求匹配器-[1]-成功");
        return permitAllMatcher;
    }

    @RefreshScope
    @Bean(AUTHENTICATED_EXCHANGE_MATCHER_BEAN_NAME)
    ServerWebExchangeMatcher authenticatedServerWebExchangeMatcher(AuthorizationProperties authorizationProperties) {
        log.info("创建认证后可访问的请求匹配器-[0]-开始");
        Map<HttpMethod, List<String>> authenticated = authorizationProperties.getAuthenticated();
        ServerWebExchangeMatcher authenticatedMather = AuthorizationManagerHelper.orServerWebExchangeMatcher(authenticated);
        log.info("创建认证后可访问的请求匹配器-[1]-成功");
        return authenticatedMather;
    }

    /**
     * 其余所有请求都走改实现判断访问是否授权
     *
     * @return 基于已授权API判断是否可访问的管理器
     */
    @Bean
    ApiAuthorizationManager apiAuthorizationManager() {
        return new ApiAuthorizationManager();
    }

}
