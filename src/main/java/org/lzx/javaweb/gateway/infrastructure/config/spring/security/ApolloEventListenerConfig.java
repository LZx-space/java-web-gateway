package org.lzx.javaweb.gateway.infrastructure.config.spring.security;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteRefreshListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * @author LZx
 * @since 2022/1/27
 */
@Slf4j
@Component
public class ApolloEventListenerConfig {

    private final ConfigDataContextRefresher contextRefresher;

    public ApolloEventListenerConfig(ConfigDataContextRefresher contextRefresher) {
        this.contextRefresher = contextRefresher;
    }

    /**
     * 监听Apollo配置变更事件，以下说明如何在更新{@link RefreshScope}代理对象外还会更新route
     * <ul>
     *     <li>{@link ConfigDataContextRefresher#refresh()}调用{@link RefreshScope#refreshAll()}</li>
     *     <li>{@link RefreshScope#refreshAll()}发布{@link RefreshScopeRefreshedEvent}事件</li>
     *     <li>{@link RouteRefreshListener#onApplicationEvent(ApplicationEvent)}监听事件后再发布
     *     {@link RefreshRoutesEvent}事件</li>
     *     <li>{@link CachingRouteLocator#onApplicationEvent(RefreshRoutesEvent)}监听到事件后清除其缓存</li>
     *     <li>{@link CachingRouteLocator#onApplicationEvent(RefreshRoutesEvent)}发现缓存中没有
     *     {@link Route}则再次根据当前配置生成所有route</li>
     * </ul>
     *
     * @param changeEvent 变更事件
     */
    @ApolloConfigChangeListener(interestedKeyPrefixes = GatewayProperties.PREFIX)
    void onGatewayConfigChange(ConfigChangeEvent changeEvent) {
        changeEvent.changedKeys().forEach(key -> log.info("配置[{}]已变更", key));
        contextRefresher.refresh();
    }

}
