package org.lzx.javaweb.gateway.infrastructure.config.spring.security.userdetails.manager;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author LZx
 * @since 2021/12/12
 */
@FeignClient(name = "userDetailsClient", url = "http://localhost:9000", configuration = ClientConfig.class)
public interface UserDetailsClient {

    @GetMapping(value = "/users/{username}")
    String findByUsername(@PathVariable("username") String username);

}
