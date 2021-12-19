package org.lzx.web.gateway.infrastructure.config.security.request.validator;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;


/**
 * SQL注入请求参数校验器
 *
 * @author LZx
 * @since 2021/9/28
 */
public class SqlInjectionValidator extends AbstractParamsValidator {

    @Override
    Mono<Void> params(MultiValueMap<String, String> params) {
        return null;
    }

    @Override
    Mono<Void> body(MediaType mediaType, DataBuffer dataBuffer) {
        return null;
    }

}
