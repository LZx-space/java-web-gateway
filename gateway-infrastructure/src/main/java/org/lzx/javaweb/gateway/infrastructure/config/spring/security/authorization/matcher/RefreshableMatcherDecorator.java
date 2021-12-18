package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.matcher;

import lombok.extern.slf4j.Slf4j;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.RequestPathProperties;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.event.RefreshPermissionEvent;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 可运行时刷新的请求匹配器
 *
 * @author LZx
 * @since 2021/12/16
 */
@Slf4j
public class RefreshableMatcherDecorator implements ServerWebExchangeMatcher, ApplicationListener<RefreshPermissionEvent> {

    private ServerWebExchangeMatcher serverWebExchangeMatcher;

    private final Class<? extends RequestPathProperties> supportedEventSource;

    public RefreshableMatcherDecorator(Class<? extends RequestPathProperties> supportedEventSource) {
        this.supportedEventSource = supportedEventSource;
    }

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return serverWebExchangeMatcher.matches(exchange);
    }

    @Override
    public void onApplicationEvent(RefreshPermissionEvent event) {
        RequestPathProperties requestPathProperties = (RequestPathProperties) event.getSource();
        if (!isSupportedEventSource(requestPathProperties.getClass())) {
            return;
        }
        OrServerWebExchangeMatcher orServerWebExchangeMatcher = AuthorizationManagerHelper.serverWebExchangeMatcher(requestPathProperties);
        log.info("更新授权配置，获取新的请求匹配器[{}]", orServerWebExchangeMatcher);
        this.serverWebExchangeMatcher = orServerWebExchangeMatcher;
    }

    /**
     * 判断是否支持指定类型配置文件更新的事件
     *
     * @param propertiesType 配置文件类型
     * @return 支持该配置文件刷新事件则true
     */
    private boolean isSupportedEventSource(Class<? extends RequestPathProperties> propertiesType) {
        return propertiesType.isAssignableFrom(this.supportedEventSource);
    }

}
