package org.lzx.web.gateway.infrastructure.config.security;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.sql.rowset.serial.SerialRef;

/**
 * @author LZx
 * @since 2021/10/1
 */
public interface MatcherSecurityFilter extends WebFilter {

    Mono<Boolean> matches(ServerWebExchange exchange);

    default Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ServerWebExchangeUtils.cacheRequestBody(exchange, decoratedRequest -> {
            MediaType contentType = decoratedRequest.getHeaders().getContentType();
            MultiValueMap<String, String> queryParams = decoratedRequest.getQueryParams();
            DataBuffer dataBuffer = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
            handler(decoratedRequest, dataBuffer);
            ServerWebExchange mutatedExchange = exchange.mutate().request(decoratedRequest).build();
            return chain.filter(mutatedExchange);
        });
    }

    Mono<Void> handler(ServerHttpRequest request, DataBuffer dataBuffer);

}
