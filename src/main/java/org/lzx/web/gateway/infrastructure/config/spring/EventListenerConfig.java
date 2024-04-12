package org.lzx.web.gateway.infrastructure.config.spring.security;

import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteRefreshListener;
import org.springframework.context.ApplicationEvent;

/**
 * @author LZx
 * @since 2022/1/27
 */
public class EventListenerConfig {

    private final ConfigDataContextRefresher contextRefresher;

    public EventListenerConfig(ConfigDataContextRefresher contextRefresher) {
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
     */
    void onGatewayConfigChange() {
        contextRefresher.refresh();
    }

}
