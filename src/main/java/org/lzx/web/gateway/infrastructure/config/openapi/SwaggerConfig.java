package org.lzx.web.gateway.infrastructure.config.openapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Swagger的配置
 *
 * @author LZx
 * @since 2021/9/28
 */
@Slf4j
@Configuration
public class SwaggerConfig {

    /**
     * 整合网关本地的API文档资源和被路由服务的API文档资源
     *
     * @param environment        所有的API文档资源提供器
     * @param documentationCache 所有的API文档资源提供器
     * @param pluginsManager     所有的API文档资源提供器
     * @param gatewayProperties  网关配置
     * @param swaggerProperties  Swagger配置
     * @return 合并所有资源提供器的文档资源提供器
     */
    @Bean
    @Primary
    @RefreshScope
    SwaggerResourcesProvider combinedSwaggerResourcesProvider(
            Environment environment,
            DocumentationCache documentationCache,
            DocumentationPluginsManager pluginsManager,
            ApplicationContext applicationContext,
            GatewayProperties gatewayProperties,
            SwaggerProperties swaggerProperties) {
        log.info("创建合并的Swagger资源提供器-[1]-开始");
        InMemorySwaggerResourcesProvider localResourcesProvider = new InMemorySwaggerResourcesProvider(
                environment, documentationCache, pluginsManager);
        localResourcesProvider.setApplicationContext(applicationContext);

        RoutedSwaggerResourcesProvider routedResourcesProvider = new RoutedSwaggerResourcesProvider(
                gatewayProperties, swaggerProperties);

        SwaggerResourcesProvider supplier = () -> Stream.of(localResourcesProvider, routedResourcesProvider)
                .flatMap(provider -> provider.get().stream())
                .collect(Collectors.toList());
        log.info("创建合并的Swagger资源提供器-[2]-成功");
        return supplier;
    }

}
