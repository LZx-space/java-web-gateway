package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

/**
 * 将JWT格式的Access Token通过Authentication Bearer Header认证，其认证对象的处理
 *
 * @author LZx
 * @since 2021/12/21
 */
public class JwtTokenConverter implements AuthenticationTokenConverter {

    @Override
    public boolean isSupport(Authentication authentication) {
        return authentication instanceof JwtAuthenticationToken;
    }

    @Override
    public Map<String, Object> convert(Authentication authentication) {
        return null;
    }

}
