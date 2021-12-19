package org.lzx.web.gateway.infrastructure.config.spring.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.model.Response;
import org.lzx.web.gateway.infrastructure.util.JsonUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author LZx
 * @since 2021/12/11
 */
@Slf4j
@Component
public class DefaultAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("success[{}]", authentication.getName());
        try {
            String json = JsonUtils.write(Response.ok());
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            Mono<DataBuffer> map = Mono.just(json)
                    .map(e -> e.getBytes(StandardCharsets.UTF_8))
                    .map(e -> response.bufferFactory().wrap(e));
            return response.writeAndFlushWith(Mono.just(map));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

}
