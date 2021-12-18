package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.upstream;

import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证信息转换器默认实现
 *
 * @author LZx
 * @since 2021/9/28
 */
@Slf4j
public class DefaultAuthenticationTokenConverter implements AuthenticationTokenConverter {

    @Override
    public String convert(Authentication source) {
        if (source instanceof AnonymousAuthenticationToken) {
            return null;
        }
        if (!source.isAuthenticated()) {
            return null;
        }
        Map<String, Object> simpleInfo = new HashMap<>();
        simpleInfo.put("username", source.getName());
        simpleInfo.put("authorities", source.getAuthorities());
        String json = JSONObjectUtils.toJSONString(simpleInfo);
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

}
