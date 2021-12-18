package org.lzx.javaweb.gateway.infrastructure.config.security.request.validator;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;


/**
 * XSS请求参数校验器
 *
 * @author LZx
 * @since 2021/9/28
 */
public class XssValidator extends AbstractParamsValidator {

    @Override
    Mono<Void> params(MultiValueMap<String, String> params) {
        return null;
    }

    @Override
    Mono<Void> body(MediaType mediaType, DataBuffer dataBuffer) {
        return null;
    }

}
