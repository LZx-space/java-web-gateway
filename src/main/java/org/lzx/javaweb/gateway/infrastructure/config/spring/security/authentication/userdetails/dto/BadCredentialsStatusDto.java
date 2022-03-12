package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 错误凭证状态
 *
 * @author LZx
 * @since 2022/1/26
 */
@Data
public class BadCredentialsStatusDto implements Serializable {

    private boolean accountNonLocked;

    private int badCredentialsSum;

    private int badCredentialsRemaining;

    private int lockMinutes;

}
