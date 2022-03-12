package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.BadCredentialsStatusReactiveUserDetailsService;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.BadCredentialsStatusDto;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.manager.RemoteUserDetailsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Slf4j
public class BadCredentialsStatusReactiveUserDetailsServiceImpl implements BadCredentialsStatusReactiveUserDetailsService {

    private final RemoteUserDetailsClient userDetailsClient;

    public BadCredentialsStatusReactiveUserDetailsServiceImpl(RemoteUserDetailsClient userDetailsClient) {
        this.userDetailsClient = userDetailsClient;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return Mono.error(new InternalAuthenticationServiceException("用户名不能为空"));
        }
        return userDetailsClient.findByUsername(username).map(userDto -> userDto);
    }

    @Override
    public Mono<BadCredentialsStatusDto> handleBadCredentialsEvent(String username) {
        return userDetailsClient.handleBadCredentialsEvent(username);
    }

}
