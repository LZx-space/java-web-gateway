package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;

/**
 * 将授权的API与角色关联的权限对象
 *
 * @author LZx
 * @since 2021/9/26
 */
@EqualsAndHashCode
public class ApiGrantedAuthority implements GrantedAuthority {

    /**
     * 对于UsernamePasswordAuthenticationToken这里是role
     * 对于OAuth2、OIDC相关的认证令牌这里是scope
     */
    private final String authority;

    private final Collection<AntPathApi> permittedApis = new HashSet<>();

    @JsonCreator
    public ApiGrantedAuthority(@JsonProperty("authority") String authority, @JsonProperty("permittedApis") Collection<AntPathApi> permittedApis) {
        Assert.hasText(authority, "A granted authority textual representation is required");
        Assert.notNull(permittedApis, "granted apis must not be null");
        this.authority = authority;
        this.permittedApis.addAll(permittedApis);
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public Collection<AntPathApi> getPermittedApis() {
        return this.permittedApis;
    }

}
