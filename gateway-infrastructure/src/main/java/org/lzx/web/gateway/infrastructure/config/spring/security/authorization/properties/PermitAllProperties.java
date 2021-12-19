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
public class PermitAllProperties implements RequestPathProperties {

    /**
     * 无论是否认证都能访问
     */
    private Map<HttpMethod, List<String>> permitAll = new HashMap<>();

    @Override
    public Map<HttpMethod, List<String>> getMethodPathPatterns() {
        return permitAll;
    }

}
