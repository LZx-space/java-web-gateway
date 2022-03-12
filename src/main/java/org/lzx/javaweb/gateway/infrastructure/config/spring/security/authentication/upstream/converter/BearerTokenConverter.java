package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * OAuth2 Bearer认证的JWT认证信息转换器
 *
 * @author LZx
 * @since 2021/12/24
 */
public class BearerTokenConverter implements AuthenticationTokenConverter {

    @Override
    public boolean isSupport(Authentication authentication) {
        return false;
    }

    @Override
    public Map<String, Object> convert(@NonNull Authentication source) {
        return null;
    }

}
