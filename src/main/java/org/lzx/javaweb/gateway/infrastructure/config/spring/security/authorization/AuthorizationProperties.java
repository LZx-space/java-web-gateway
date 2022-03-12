package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.pattern.PathPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LZx
 * @since 2021/12/17
 */
@Slf4j
@Data
@ConfigurationProperties(AuthorizationProperties.AUTHORIZATION_PROPERTIES_PREFIX)
public class AuthorizationProperties implements InitializingBean {

    public static final String AUTHORIZATION_PROPERTIES_PREFIX = "spring.cloud.gateway.security.authorization";

    /**
     * 路径应符合的正则，特殊符号为{@link PathPattern}捕捉路径变量的标识符
     */
    private static final String PATH_PATTERN_REGEX = "[a-zA-Z0-9_\\-/*.,${}:|\\[\\]]+";

    /**
     * 拒绝经过网关的任何如下请求的访问
     */
    private Map<HttpMethod, List<String>> denyAll = new HashMap<>();

    /**
     * 无论是否认证都能访问
     */
    private Map<HttpMethod, List<String>> permitAll = new HashMap<>();

    /**
     * 已认证的用户能访问的请求的路径匹配
     */
    private Map<HttpMethod, List<String>> authenticated = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        denyAll.forEach((method, patterns) -> patterns.forEach(pattern -> {
            if (!pattern.matches(PATH_PATTERN_REGEX)) {
                throw new IllegalArgumentException("校验拒绝任意外部访问方法配置，请求方法[" + method + "]的路径匹配字符串[" + pattern + "]格式错误");
            }
        }));
        permitAll.forEach((method, patterns) -> patterns.forEach(pattern -> {
            if (!pattern.matches(PATH_PATTERN_REGEX)) {
                throw new IllegalArgumentException("校验允许所有人方法配置，请求方法[" + method + "]的路径匹配字符串[" + pattern + "]格式错误");
            }
        }));
        authenticated.forEach((method, patterns) -> patterns.forEach(pattern -> {
            if (!pattern.matches(PATH_PATTERN_REGEX)) {
                throw new IllegalArgumentException("校验已登录可访问方法配置，请求方法[" + method + "]的路径匹配字符串[" + pattern + "]格式错误");
            }
        }));
        log.info("成功创建授权配置属性绑定对象，标识[{}]", this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
    }

}
