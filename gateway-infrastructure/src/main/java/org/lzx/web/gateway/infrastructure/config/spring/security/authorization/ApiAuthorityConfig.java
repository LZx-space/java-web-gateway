package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.evaluator.ApiAuthorizationEvaluator;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.evaluator.AuthenticatedAuthorizationEvaluator;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.evaluator.PermitAllAuthorizationEvaluator;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.properties.AuthenticatedProperties;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.properties.PermitAllProperties;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import java.util.List;

/**
 * @author LZx
 * @since 2021/9/29
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({PermitAllProperties.class, AuthenticatedProperties.class})
public class ApiAuthorityConfig {

    @Bean
    DefaultAuthorizationManager apiAuthorizationManager(List<AuthorizationEvaluator> authorizationEvaluators) {
        return new DefaultAuthorizationManager(authorizationEvaluators);
    }

    @Bean
    @RefreshScope
    @Order(Ordered.HIGHEST_PRECEDENCE)
    AuthorizationEvaluator permitAllAuthorizationEvaluator(PermitAllProperties permitAllProperties) {
        log.info("API权限控制-开始[0]-创建任何人可访问请求匹配器");
        ServerWebExchangeMatcher permitAllMatcher = AuthorizationManagerHelper.serverWebExchangeMatcher(permitAllProperties);
        log.info("API权限控制-结束[1]-创建任何人可访问请求匹配器");
        return new PermitAllAuthorizationEvaluator(permitAllMatcher);
    }

    @Bean
    @RefreshScope
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    AuthorizationEvaluator authenticatedAuthorizationEvaluator(AuthenticatedProperties authenticatedProperties) {
        log.info("API权限控制-开始[0]-创建登录后可访问请求匹配器");
        ServerWebExchangeMatcher authenticatedMather = AuthorizationManagerHelper.serverWebExchangeMatcher(authenticatedProperties);
        log.info("API权限控制-结束[1]-创建登录后可访问请求匹配器");
        return new AuthenticatedAuthorizationEvaluator(authenticatedMather);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 2)
    AuthorizationEvaluator apiAuthorizationEvaluator() {
        return new ApiAuthorizationEvaluator();
    }

}
