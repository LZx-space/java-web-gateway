package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;

/**
 * 请求路径匹配表达式MAP，HTTP方法作为Key，值为路径匹配表达式字符串集合
 *
 * @author LZx
 * @since 2021/12/17
 */
public class RequestPathPatternMap extends HashMap<HttpMethod, List<String>> {

}
