package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.event;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.RequestPathProperties;
import org.springframework.context.ApplicationEvent;

/**
 * @author LZx
 * @since 2021/12/17
 */
public class RefreshPermissionEvent extends ApplicationEvent {

    public RefreshPermissionEvent(RequestPathProperties requestPathProperties) {
        super(requestPathProperties);
    }

}
