package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.exception.DefaultPasswordException;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * 成功检索到用户并核对密码成功后，额外再使用的用户数据检查器
 *
 * @author LZx
 * @since 2021/12/22
 */
@Slf4j
public class PostUserDetailsChecker implements UserDetailsChecker {

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public void check(UserDetails toCheck) {
        if (!toCheck.isCredentialsNonExpired()) {
            log.debug("User account credentials have expired");
            throw new CredentialsExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
        }
        if (!(toCheck instanceof UserDto)) {
            log.error("受检用户的类型不为[{}]，已跳过认证后检查", UserDto.class.getName());
            return;
        }
        UserDto userDto = (UserDto) toCheck;
        checkIfNeedChangeDefaultPwd(userDto);
    }

    /**
     * 检查是否需要默认密码
     *
     * @param toCheck 用户信息
     * @throws AuthenticationException 认证异常
     */
    private void checkIfNeedChangeDefaultPwd(UserDto toCheck) throws AuthenticationException {
        if (toCheck.isDefaultPassword()) {
            throw new DefaultPasswordException("需求修改默认密码");
        }
    }

}
