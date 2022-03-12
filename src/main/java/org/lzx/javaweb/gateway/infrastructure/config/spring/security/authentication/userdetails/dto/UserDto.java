package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Data
@JsonIgnoreProperties("authorities")
public class UserDto implements UserDetails, CredentialsContainer {

    private String password;

    private String username;

    private Set<? extends GrantedAuthority> authorities;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private boolean defaultPassword;

    /**
     * 生效期内首次错误时间
     */
    private Date firstBadCredentialsTimeInValidity;

    /**
     * 因为错误密码导致锁定账户的时间
     */
    private Date badCredentialsLockTime;

    /**
     * 记录错误次数的有效期内密码错误的次数
     */
    private int badCredentialsTimesInValidity;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
