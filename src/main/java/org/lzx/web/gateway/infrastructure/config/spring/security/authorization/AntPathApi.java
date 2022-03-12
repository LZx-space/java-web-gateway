package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public AntPathApi(@JsonProperty("method") HttpMethod method, @JsonProperty("antPathUrl") String antPathUrl) {
        this.method = method;
        this.antPathUrl = antPathUrl;
    }

}
