package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.userdetails.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author LZx
 * @since 2021/12/12
 */
public class SimpleGrantedAuthorityDto implements GrantedAuthority {

    private final String role;

    @JsonCreator
    public SimpleGrantedAuthorityDto(@JsonProperty("authority") String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimpleGrantedAuthorityDto) {
            return this.role.equals(((SimpleGrantedAuthorityDto) obj).role);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }

}
