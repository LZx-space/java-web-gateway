package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

/**
 * MVC风格的API路径
 *
 * @author LZx
 * @since 2021/9/26
 */
@Data
public class AntPathApi implements Serializable {

    private HttpMethod method;

    private String antPathUrl;

    public AntPathApi(HttpMethod method, String antPathUrl) {
        this.method = method;
        this.antPathUrl = antPathUrl;
    }

}
