package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.properties;

import lombok.Data;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.RequestPathProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/17
 */
@Data
@ConfigurationProperties("spring.cloud.gateway.security.api-authority")
public class AuthenticatedProperties implements RequestPathProperties {

    /**
     * 已认证的用户能访问的请求的路径匹配
     */
    private Map<HttpMethod, List<String>> authenticated = new HashMap<>();

    @Override
    public Map<HttpMethod, List<String>> getMethodPathPatterns() {
        return authenticated;
    }

}
