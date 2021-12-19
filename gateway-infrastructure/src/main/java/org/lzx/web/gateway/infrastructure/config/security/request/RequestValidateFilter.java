package org.lzx.web.gateway.infrastructure.config.security.request;

import org.lzx.web.gateway.infrastructure.config.security.MatcherSecurityFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author LZx
 * @since 2021/10/1
 */
public class RequestValidateFilter implements MatcherSecurityFilter {

    private List<RequestValidator> validators;

    @Override
    public Mono<Boolean> matches(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<Void> handler(ServerHttpRequest request, DataBuffer dataBuffer) {
        return Flux.fromIterable(validators)
                .next()
                .flatMap(validators -> validators.validate(request, dataBuffer));
    }
}
