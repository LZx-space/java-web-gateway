package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.util;

import lombok.extern.slf4j.Slf4j;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.RequestPathProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LZx
 * @since 2021/12/14
 */
@Slf4j
public class AuthorizationManagerHelper {

    private static final PathPatternParser pathPatternParser = new PathPatternParser();

    private AuthorizationManagerHelper() {
    }

    private static final AuthenticationTrustResolver authTrustResolver = new AuthenticationTrustResolverImpl();

    /**
     * 判断请求是否符合指定请求路径正则
     *
     * @param requestPathPatterns 请求路径正则
     * @return Predicate
     */
    public static Predicate<ServerWebExchange> webExchangePredicate(Map<HttpMethod, List<PathPattern>> requestPathPatterns) {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            PathContainer path = PathContainer.parsePath(request.getURI().getRawPath());
            return requestPathPatterns.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(request.getMethod()))
                    .flatMap(entry -> entry.getValue().stream())
                    .anyMatch(pattern -> pattern.matches(path));
        };
    }

    public static Object t(Map<HttpMethod, List<String>> methodPathMap) {
        methodPathMap.forEach((httpMethod, value) -> {
            List<PathPattern> pathPatterns = value.stream()
                    .peek(v -> log.info("[*]-[{}\t]-[{}]", httpMethod, v))
                    .map(pathPatternParser::parse)
                    .collect(Collectors.toList());
            // todo 如果更新后的方法不全，老的不会被覆盖，BUG
//            permitAllRequestPathPatterns.put(httpMethod, pathPatterns);
        });
        return null;
    }

    /**
     * 将请求路径正则转换为请求匹配器
     *
     * @param requestPathProperties 请求方法为Key路径匹配表达式为Value类型的属性
     * @return 请求匹配器
     */
    public static OrServerWebExchangeMatcher serverWebExchangeMatcher(RequestPathProperties requestPathProperties) {
        PathPatternParserServerWebExchangeMatcher[] matchers = requestPathProperties.getMethodPathPatterns().entrySet().stream()
                .flatMap(e -> {
                    HttpMethod method = e.getKey();
                    List<String> pathPatterns = e.getValue();
                    List<PathPattern> patterns = pathPatterns.stream()
                            .map(pathPatternParser::parse)
                            .collect(Collectors.toList());
                    return patterns.stream()
                            .map(pattern -> new PathPatternParserServerWebExchangeMatcher(pattern, method));
                })
                .toArray(PathPatternParserServerWebExchangeMatcher[]::new);
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
