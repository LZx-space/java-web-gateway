package org.lzx.javaweb.gateway.infrastructure.config.spring.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Configuration
@EnableFeignClients(basePackages = "org.lzx.javaweb.gateway")
public class FeignConfig {

}
