package org.lzx.web.gateway.infrastructure.config.security.request.validator;

import org.lzx.web.gateway.infrastructure.config.security.request.RequestValidator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/**
 * 请求参数校验器抽象类
 *
 * @author LZx
 * @since 2021/9/28
 */
public abstract class AbstractParamsValidator implements RequestValidator {

    @Override
    public Mono<Void> validate(ServerHttpRequest request, DataBuffer dataBuffer) {
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        return null;
    }

    abstract Mono<Void> params(MultiValueMap<String, String> params);

    abstract Mono<Void> body(MediaType mediaType, DataBuffer dataBuffer);

}
