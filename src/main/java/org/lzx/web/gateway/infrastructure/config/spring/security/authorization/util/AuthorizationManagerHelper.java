package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.util;

import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LZx
 * @since 2021/12/14
 */
@Slf4j
public class AuthorizationManagerHelper {

    private AuthorizationManagerHelper() {
    }

    private static final AuthenticationTrustResolver AUTH_TRUST_RESOLVER = new AuthenticationTrustResolverImpl();

    /**
     * 将请求路径正则转换为请求匹配器
     *
     * @param requestPathProperties Key为请求方法Value为路径匹配表达式的MAP
     * @return 请求匹配器
     */
    public static OrServerWebExchangeMatcher orServerWebExchangeMatcher(Map<HttpMethod, List<String>> requestPathProperties) {
        List<ServerWebExchangeMatcher> matchers = requestPathProperties.entrySet().stream()
                .sorted(Comparator.comparing(e -> {
                    HttpMethod method = e.getKey();
                    return method.ordinal();
                }))
                .map(e -> {
                    HttpMethod method = e.getKey();
                    // 此处排序用于查看日志，实际对于一个同一个方法的所有路径匹配式，PathPattern类内部已经排序，简单来讲排序规则为
                    // 含统配符等匹配条件的表达式其涵盖范围约小就优先级越高
                    // 无需模糊匹配的明确路径则路径越长优先级越高
                    String[] sortedPathPatterns = e.getValue().stream()
                            .sorted(String::compareToIgnoreCase)
                            .toArray(String[]::new);
                    log.info("新建路径匹配器-[{}]-[{}]", String.format("%-7s", method), sortedPathPatterns);
                    // org.springframework.http.HttpMethod.resolve(String method)一个不存在方法等同于下面方法，只是如下显示描述
                    if (HttpMethod.ALL.equals(method)) {
                        return ServerWebExchangeMatchers.pathMatchers(sortedPathPatterns);
                    }
                    return ServerWebExchangeMatchers.pathMatchers(
                            org.springframework.http.HttpMethod.valueOf(method.name()),
                            sortedPathPatterns);
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
        return !AUTH_TRUST_RESOLVER.isAnonymous(authentication);
    }

}
