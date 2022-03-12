package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * 用户密码需要强制修改的异常
 *
 * @author LZx
 * @since 2020/10/27
 */
public class DefaultPasswordException extends AccountStatusException {

    public DefaultPasswordException(String msg) {
        super(msg);
    }

}
