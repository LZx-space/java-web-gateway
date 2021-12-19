package org.lzx.web.gateway.infrastructure.config.security.request;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * 请求验证器
 *
 * @author LZx
 * @since 2021/9/28
 */
public interface RequestValidator {

    /**
     * 验证请求
     *
     * @param request 请求
     * @return nothing
     */
    Mono<Void> validate(ServerHttpRequest request, DataBuffer dataBuffer);

}
