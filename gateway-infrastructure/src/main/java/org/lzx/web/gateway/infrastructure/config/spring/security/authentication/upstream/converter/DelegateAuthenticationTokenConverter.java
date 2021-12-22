package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/21
 */
public class DelegateAuthenticationTokenConverter implements AuthenticationTokenConverter {

    private final List<AuthenticationTokenConverter> converters;

    public DelegateAuthenticationTokenConverter(List<AuthenticationTokenConverter> converters) {
        this.converters = converters;
    }

    @Override
    public boolean isSupport(Authentication authentication) {
        return true;
    }

    @Override
    public Map<String, Object> convert(Authentication authentication) {
        return converters.stream()
                .filter(e -> e.isSupport(authentication))
                .findFirst()
                .map(e -> e.convert(authentication))
                .orElseThrow(() -> new RuntimeException("该认证对象无适配的转换器"));
    }

}
