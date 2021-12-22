package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.converter;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenConverter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/22
 */
public class AnonymousTokenConverter implements AuthenticationTokenConverter {

    @Override
    public boolean isSupport(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public Map<String, Object> convert(Authentication source) {
        return Collections.emptyMap();
    }

}
