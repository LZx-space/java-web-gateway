package org.lzx.javaweb.gateway.infrastructure.config.spring.security.userdetails.manager;

import feign.codec.Decoder;
import feign.codec.StringDecoder;
import org.springframework.context.annotation.Bean;

/**
 * @author LZx
 * @since 2021/12/12
 */
public class ClientConfig {

    @Bean
    Decoder decoder() {
        return new StringDecoder();
    }

}
