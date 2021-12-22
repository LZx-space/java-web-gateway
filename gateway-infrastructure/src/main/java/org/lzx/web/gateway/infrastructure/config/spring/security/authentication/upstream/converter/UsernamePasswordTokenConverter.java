package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证信息转换器默认实现
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
        if (!source.isAuthenticated()) {
            return Collections.emptyMap();
        }
        Map<String, Object> simpleInfo = new HashMap<>();
        simpleInfo.put("username", source.getName());
        simpleInfo.put("authorities", source.getAuthorities());
        return simpleInfo;
    }

}
