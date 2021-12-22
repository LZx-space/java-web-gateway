package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LZx
 * @since 2021/12/17
 */
@Data
@ConfigurationProperties(AuthorizationProperties.AUTHORIZATION_PROPERTIES_PREFIX)
public class AuthorizationProperties {

    static final String AUTHORIZATION_PROPERTIES_PREFIX = "spring.cloud.gateway.security.authorization";

    /**
     * 无论是否认证都能访问
     */
    private RequestPathPatternMap permitAll = new RequestPathPatternMap();

    /**
     * 已认证状态可访问
     */
    private RequestPathPatternMap authenticated = new RequestPathPatternMap();

}
