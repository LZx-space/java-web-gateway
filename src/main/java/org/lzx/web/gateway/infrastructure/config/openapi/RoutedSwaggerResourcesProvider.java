package org.lzx.web.gateway.infrastructure.config.openapi;

import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.support.NameUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文档资源提供器，提供接口的文档，按服务的路由ID将文档资源分组，并按转发的匹配规则修改每个服务的文档路径
 *
 * @author LZx
 * @since 2022/2/21
 */
public class RoutedSwaggerResourcesProvider implements SwaggerResourcesProvider {

    private static final String PATH_ROUTE_PREDICATE_PREFIX = "Path";

    private final GatewayProperties gatewayProperties;

    private final SwaggerProperties swaggerProperties;

    public RoutedSwaggerResourcesProvider(GatewayProperties gatewayProperties, SwaggerProperties swaggerProperties) {
        this.gatewayProperties = gatewayProperties;
        this.swaggerProperties = swaggerProperties;
    }

    @Override
    public List<SwaggerResource> get() {
        return gatewayProperties.getRoutes().stream()
                .map(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> PATH_ROUTE_PREDICATE_PREFIX
                                .equalsIgnoreCase(predicateDefinition.getName())
                        )
                        .findFirst()
                        .map(predicateDefinition -> {
                            Optional<SwaggerProperties.RoutedServiceConfig> config = getRoutedServiceConfig(
                                    swaggerProperties, routeDefinition.getId());
                            if (!config.isPresent()) {
                                return null;
                            }
                            SwaggerProperties.RoutedServiceConfig routedServiceConfig = config.get();
                            if (!routedServiceConfig.isIncluded()) {
                                return null;
                            }
                            String predicatePath = predicateDefinition.getArgs().get(NameUtils.generateName(0));
                            // 替换Path参数值中的/**为API文档接口地址
                            String apiDocUrl = predicatePath.replace("/**", routedServiceConfig.getApiDocUrl());
                            return resource(routedServiceConfig.getServiceName(), apiDocUrl);
                        })
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private SwaggerResource resource(String swaggerGroup, String baseUrl) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(swaggerGroup);
        swaggerResource.setUrl(baseUrl);
        return swaggerResource;
    }

    private Optional<SwaggerProperties.RoutedServiceConfig> getRoutedServiceConfig(
            SwaggerProperties properties,
            String routeId) {
        return properties.getRoutedServiceConfig().stream()
                .filter(routedServiceConfig -> routedServiceConfig.getRouteId().equalsIgnoreCase(routeId))
                .findFirst();
    }

}
