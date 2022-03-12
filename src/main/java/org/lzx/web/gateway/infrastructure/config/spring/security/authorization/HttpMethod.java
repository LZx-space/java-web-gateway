package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util.AuthorizationManagerHelper;

import java.util.Map;

/**
 * 用于配置的属性映射，也决定{@link AuthorizationManagerHelper#orServerWebExchangeMatcher(Map)}中对不同HTTP方法的匹配的先后顺序
 *
 * @author LZx
 * @since 2022/1/10
 */
public enum HttpMethod {

    /**
     * 代表所有方法
     */
    ALL, GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, TRACE

}
