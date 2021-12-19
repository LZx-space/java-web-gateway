package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * 请求路径匹配表达式MAP，HTTP方法作为Key，值为路径匹配表达式字符串集合
 *
 * @author LZx
 * @since 2021/12/17
 */
public interface RequestPathProperties {

    /**
     * 获取Key为HTTP方法Value为路径匹配的MAP
     *
     * @return 请求路径匹配字符串
     */
    Map<HttpMethod, List<String>> getMethodPathPatterns();

}
