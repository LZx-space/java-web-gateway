package org.lzx.web.gateway.infrastructure.config.spring.security;

import org.lzx.web.gateway.infrastructure.model.Response;
import org.lzx.web.gateway.infrastructure.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author LZx
 * @since 2021/12/11
 */
@Slf4j
@Component
public class DefaultAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private static final String REDIRECT_URL_KEY = "redirectUrl";

    private final ServerRequestCache requestCache;

    public DefaultAuthenticationSuccessHandler(ServerRequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        Mono<Mono<DataBuffer>> dataBuffer = this.requestCache.getRedirectUri(webFilterExchange.getExchange())
                .map(uri -> {
                    HashMap<String, String> result = new HashMap<>(1);
                    result.put(REDIRECT_URL_KEY, uri.toASCIIString());
                    return result;
                })
                .defaultIfEmpty(new HashMap<>(0))
                .map(map -> {
                    Response<HashMap<String, String>> res = Response.ok(map);
                    try {
                        return JsonUtils.write(res);
                    } catch (JsonProcessingException e) {
                        log.error("序列化登录成功响应内容失败", e);
                        throw new RuntimeException(e);
                    }
                })
                .map(json -> {
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return Mono.just(buffer);
                });
        return response.writeAndFlushWith(dataBuffer);
    }

}
