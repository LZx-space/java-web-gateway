package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.BadCredentialsStatusDto;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

/**
 * 错误凭证操作服务
 *
 * @author LZx
 * @since 2022/1/26
 */
public interface BadCredentialsStatusReactiveUserDetailsService extends ReactiveUserDetailsService {

    /**
     * 处理错误凭证事件
     *
     * @param username 用户名
     * @return 账户状态信息
     */
    Mono<BadCredentialsStatusDto> handleBadCredentialsEvent(String username);

}
