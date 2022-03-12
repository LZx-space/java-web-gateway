package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/23
 */
public class DelegateAuthenticationTokenConverter implements AuthenticationTokenConverter {

    private final List<AuthenticationTokenConverter> authenticationTokenConverters;

    public DelegateAuthenticationTokenConverter(List<AuthenticationTokenConverter> authenticationTokenConverters) {
        Assert.notEmpty(authenticationTokenConverters, "认证对象转换器不能为空");
        this.authenticationTokenConverters = authenticationTokenConverters;
    }

    @Override
    public boolean isSupport(Authentication authentication) {
        return true;
    }

    @Override
    public Map<String, Object> convert(@NonNull Authentication source) {
        // todo reactor时不要用stream，异步架构下线程有效利用率更高
        return authenticationTokenConverters.stream()
                .filter(e -> e.isSupport(source))
                .findFirst()
                .map(e -> e.convert(source))
                .orElseThrow(() -> new IllegalArgumentException("无认证信息转换器"));
    }

}
