package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;

/**
 * 转换器，将认证信息转换为字符串类型的令牌，下游服务可以从该令牌获取认证信息
 *
 * @author LZx
 * @since 2021/9/28
 */
public interface AuthenticationTokenConverter extends Converter<Authentication, String> {

}
