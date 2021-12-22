package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util;

import lombok.extern.slf4j.Slf4j;
import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.RequestPathPatternMap;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LZx
 * @since 2021/12/14
 */
@Slf4j
public class AuthorizationManagerHelper {

    private AuthorizationManagerHelper() {
    }

    private static final AuthenticationTrustResolver authTrustResolver = new AuthenticationTrustResolverImpl();

    /**
     * 将请求路径正则转换为请求匹配器
     *
     * @param requestPathPatternMap 请求方法为Key路径匹配表达式为Value类型的属性
     * @return 请求匹配器
     */
    public static ServerWebExchangeMatcher serverWebExchangeMatcher(RequestPathPatternMap requestPathPatternMap) {
        List<ServerWebExchangeMatcher> matchers = requestPathPatternMap.entrySet().stream()
                .map(e -> {
                    HttpMethod method = e.getKey();
                    String[] pathPatterns = e.getValue().toArray(String[]::new);
                    log.info("创建路径匹配器-[{}]-[{}]", String.format("%-7s", method), pathPatterns);
                    return ServerWebExchangeMatchers.pathMatchers(method, pathPatterns);
                })
                .collect(Collectors.toList());
        return new OrServerWebExchangeMatcher(matchers);
    }

    /**
     * Verify (via {@link AuthenticationTrustResolver}) that the given authentication is
     * not anonymous.
     *
     * @param authentication to be checked
     * @return <code>true</code> if not anonymous, otherwise <code>false</code>.
     */
    public static boolean isNotAnonymous(Authentication authentication) {
        return !authTrustResolver.isAnonymous(authentication);
    }

}
