package org.lzx.web.gateway.infrastructure.config.spring.security.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/12
 */
public class HeaderWritableResponseFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpResponse rawResponse = exchange.getResponse();
        return chain.filter(exchange.mutate()
                .response(new HeaderWritableResponse(rawResponse))
                .build()
        );
    }

    private static class HeaderWritableResponse extends ServerHttpResponseDecorator {

        private final HttpHeaders httpHeaders;

        public HeaderWritableResponse(ServerHttpResponse delegate) {
            super(delegate);
            httpHeaders = HttpHeaders.writableHttpHeaders(super.getHeaders());
        }

        @NonNull
        @Override
        public HttpHeaders getHeaders() {
            return this.httpHeaders;
        }

    }

}
