package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户名密码类型的认证信息转换器
 *
 * @author LZx
 * @since 2021/9/28
 */
@Slf4j
public class UsernamePasswordTokenConverter implements AuthenticationTokenConverter {

    @Override
    public boolean isSupport(Authentication authentication) {
        return authentication instanceof UsernamePasswordAuthenticationToken;
    }

    @Override
    public Map<String, Object> convert(@NonNull Authentication source) {
        return new HashMap<String, Object>(10) {{
            put("username", source.getName());
            put("authorities", source.getAuthorities());
        }};
    }

}
