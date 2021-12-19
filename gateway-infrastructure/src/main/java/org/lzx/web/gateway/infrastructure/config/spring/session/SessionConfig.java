package org.lzx.web.gateway.infrastructure.config.spring.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpCookie;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LZx
 * @since 2021/10/2
 */
@Slf4j
@EnableRedisWebSession
public class SessionConfig {

    /**
     * 对SessionId做额外处理
     *
     * @return SessionId解析器
     */
    @Bean
    public WebSessionIdResolver cookieWebSessionIdResolver() {
        return new CookieWebSessionIdResolver() {

            @Override
            public List<String> resolveSessionIds(ServerWebExchange exchange) {
                MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
                List<HttpCookie> cookies = cookieMap.get(getCookieName());
                if (cookies == null) {
                    return Collections.emptyList();
                }
                return cookies.stream().map(cookie -> handleSessionId(cookie.getValue())).collect(Collectors.toList());
            }

        };
    }

    /**
     * 原为Base64编码，后省略，留一个自定义入口
     *
     * @param sessionId 会话ID
     * @return 处理后的会话ID
     */
    private String handleSessionId(String sessionId) {
        return sessionId;
    }

}
