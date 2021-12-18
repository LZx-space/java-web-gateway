package org.lzx.javaweb.gateway.infrastructure.config.spring.security.userdetails;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.userdetails.dto.UserDto;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.userdetails.manager.UserDetailsClient;
import org.lzx.javaweb.gateway.infrastructure.util.JsonUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Service
@Primary
public class RemoteReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserDetailsClient userDetailsClient;

    public RemoteReactiveUserDetailsService(@Lazy UserDetailsClient userDetailsClient) {
        this.userDetailsClient = userDetailsClient;
    }

    /**
     * Find the {@link UserDetails} by username.
     *
     * @param username the username to look up
     * @return the {@link UserDetails}. Cannot be null
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return Mono.error(new InternalAuthenticationServiceException("用户名不能为空"));
        }
        String json = userDetailsClient.findByUsername(username);
        UserDto user;
        try {
            user = JsonUtils.read(json, UserDto.class);
        } catch (IOException e) {
            return Mono.error(new InternalAuthenticationServiceException(e.getMessage()));
        }
        if (user == null) {
            return Mono.error(new UsernameNotFoundException(username));
        }
        return Mono.just(user);
    }

}
