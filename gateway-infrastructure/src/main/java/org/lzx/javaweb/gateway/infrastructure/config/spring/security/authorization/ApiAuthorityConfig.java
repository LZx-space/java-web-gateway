package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.matcher.RefreshableMatcherDecorator;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.properties.AuthenticatedProperties;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.properties.PermitAllProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author LZx
 * @since 2021/9/29
 */
@Configuration
@EnableConfigurationProperties({PermitAllProperties.class, AuthenticatedProperties.class})
public class ApiAuthorityConfig {

    @Bean
    DefaultAuthorizationManager apiAuthorizationManager(List<AuthorizationEvaluator> authorizationEvaluators) {
        return new DefaultAuthorizationManager(authorizationEvaluators);
    }

    @Bean("m1")
    RefreshableMatcherDecorator m1() {
        return new RefreshableMatcherDecorator(PermitAllProperties.class);
    }

    @Bean("m2")
    RefreshableMatcherDecorator m2() {
        return new RefreshableMatcherDecorator(AuthenticatedProperties.class);
    }

}
