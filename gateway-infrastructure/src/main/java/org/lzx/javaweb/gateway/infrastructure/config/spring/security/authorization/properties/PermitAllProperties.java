package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.properties;

import lombok.Data;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.RequestPathProperties;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.event.RefreshPermissionEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/17
 */
@Data
@RefreshScope
@ConfigurationProperties("spring.cloud.gateway.security.api-authority")
public class PermitAllProperties implements RequestPathProperties, InitializingBean, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 无论是否认证都能访问
     */
    private Map<HttpMethod, List<String>> permitAll = new HashMap<>();

    @Override
    public Map<HttpMethod, List<String>> getMethodPathPatterns() {
        return permitAll;
    }

    @Override
    public void afterPropertiesSet() {
        applicationEventPublisher.publishEvent(new RefreshPermissionEvent(this));
    }

}
