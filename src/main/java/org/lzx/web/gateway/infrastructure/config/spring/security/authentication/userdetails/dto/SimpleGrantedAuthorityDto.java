package org.lzx.web.gateway.infrastructure.config.spring.security.authentication.userdetails.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author LZx
 * @since 2021/12/12
 */
public class SimpleGrantedAuthorityDto implements GrantedAuthority {

    private final String authority;

    @Setter
    @Getter
    private String username;

    @JsonCreator
    public SimpleGrantedAuthorityDto(@JsonProperty("authority") String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimpleGrantedAuthorityDto) {
            return this.authority.equals(((SimpleGrantedAuthorityDto) obj).authority);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.authority.hashCode();
    }

    @Override
    public String toString() {
        return this.authority;
    }

}
