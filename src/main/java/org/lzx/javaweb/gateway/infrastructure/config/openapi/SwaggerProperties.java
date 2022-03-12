package org.lzx.javaweb.gateway.infrastructure.config.openapi;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LZx
 * @since 2022/2/22
 */
@Slf4j
@Data
@ConfigurationProperties("spring.cloud.gateway.swagger.routed-services")
public class SwaggerProperties implements InitializingBean {

    private String defaultApiDocUrl = "/v3/api-docs";

    private List<RoutedServiceConfig> routedServiceConfig = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        routedServiceConfig.forEach(routedServiceConfig -> {
            String routeId = routedServiceConfig.getRouteId();
            if (!StringUtils.hasText(routeId)) {
                throw new IllegalArgumentException("路由ID不能为空");
            }
            String apiDocUrl = routedServiceConfig.getApiDocUrl();
            if (!StringUtils.hasText(apiDocUrl)) {
                routedServiceConfig.setApiDocUrl(defaultApiDocUrl);
            }
        });
    }

    @Data
    static class RoutedServiceConfig {

        private String routeId;

        private boolean included;

        private String serviceName;

        private String apiDocUrl;

    }

}
