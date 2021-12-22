package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.userdetails.manager;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author LZx
 * @since 2021/12/12
 */
@FeignClient(name = "userDetailsClient", url = "${spring.cloud.gateway.security.user-details-url}", configuration = ClientConfig.class)
public interface UserDetailsClient {

    @GetMapping(value = "/users/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    String findByUsername(@PathVariable("username") String username);

}
