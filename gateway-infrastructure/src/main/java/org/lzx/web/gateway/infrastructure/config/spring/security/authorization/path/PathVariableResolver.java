package org.lzx.web.gateway.infrastructure.config.spring.security.authorization.path;

import org.lzx.web.gateway.infrastructure.config.spring.security.authorization.path.context.PathVariableContext;

/**
 * @author LZx
 * @since 2021/11/27
 */
public interface PathVariableResolver {

    /**
     * 依据变量上下文，将URL内的标识转换为具体的数值
     *
     * @param pathVariableUrl 含有路径变量的URL
     * @return 解析处理后的URL
     */
    String handle(PathVariableContext context, String pathVariableUrl);

}
