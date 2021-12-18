package org.lzx.javaweb.gateway.infrastructure.config.spring.security.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/12
 */
public class HeaderWritableResponseFilter implements WebFilter {

    /**
     * Process the Web request and (optionally) delegate to the next
     * {@code WebFilter} through the given {@link WebFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
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

        @Override
        public HttpHeaders getHeaders() {
            return this.httpHeaders;
        }

    }

}
