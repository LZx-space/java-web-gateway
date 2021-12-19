package org.lzx.web.gateway.infrastructure.config.spring.security.authorization;

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
public class ApiGrantedAuthority implements GrantedAuthority {

    private final String role;

    private final Collection<AntPathApi> permittedApis = new HashSet<>();

    public ApiGrantedAuthority(String role, Collection<AntPathApi> permittedApis) {
        Assert.hasText(role, "A granted authority textual representation is required");
        Assert.notNull(permittedApis, "granted apis must not be null");
        this.role = role;
        this.permittedApis.addAll(permittedApis);
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public Collection<AntPathApi> getPermittedApis() {
        return this.permittedApis;
    }

}
