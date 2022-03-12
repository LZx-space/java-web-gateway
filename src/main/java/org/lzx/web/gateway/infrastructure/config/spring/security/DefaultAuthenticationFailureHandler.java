package org.lzx.web.gateway.infrastructure.config.spring.security;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.BadCredentialsStatusReactiveUserDetailsService;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.exception.DefaultPasswordException;
import org.lzx.web.gateway.infrastructure.model.Response;
import org.lzx.web.gateway.infrastructure.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author LZx
 * @since 2021/12/11
 */
@Slf4j
@Component
public class DefaultAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private static final String USERNAME_PARAMETER = "username";

    private final BadCredentialsStatusReactiveUserDetailsService userDetailsService;

    public DefaultAuthenticationFailureHandler(BadCredentialsStatusReactiveUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        Mono<Mono<DataBuffer>> dataBuffer = webFilterExchange.getExchange().getFormData()
                .flatMap(fd -> {
                    String username = fd.getFirst(USERNAME_PARAMETER);
                    return mappingResponse(exception, username);
                })
                .map(res -> {
                    try {
                        return JsonUtils.write(res);
                    } catch (JsonProcessingException e) {
                        log.error("序列化登录异常响应内容失败", e);
                        throw new RuntimeException(e);
                    }
                })
                .map(json -> {
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return Mono.just(buffer);
                });
        return response.writeAndFlushWith(dataBuffer);
    }

    /**
     * 映射异常的对应的错误码
     *
     * @param exception 认证异常
     * @param username  用户名
     * @return 错误码
     */
    private Mono<Response<Void>> mappingResponse(AuthenticationException exception, String username) {
        // flux版本的认证逻辑很多地方都未做国际化
        // see AbstractUserDetailsReactiveAuthenticationManager#authenticate(Authentication)
        String i18nMsg = "Invalid Credentials".equals(exception.getMessage()) ? "用户名或密码错误"
                : exception.getMessage();
        if (exception instanceof BadCredentialsException) {
            return userDetailsService
                    .findByUsername(username)
                    .flatMap(userDetails -> userDetailsService.handleBadCredentialsEvent(userDetails.getUsername()))
                    .map(statusDto -> {
                        int badCredentialsSum = statusDto.getBadCredentialsSum();
                        if (statusDto.isAccountNonLocked()) {
                            log.info("账户[{}]登录失败-[使用错误的密码尝试登录{}次]", username, badCredentialsSum);
                            int badCredentialsRemaining = statusDto.getBadCredentialsRemaining();
                            String uiMessage = String.format("密码错误，剩余尝试次数%s", badCredentialsRemaining);
                            return Response.fail("A0410", uiMessage);
                        } else {
                            int lockMinutes = statusDto.getLockMinutes();
                            log.warn("账户[{}]登录失败-[使用错误的密码尝试登录{}次且账户更新为锁定状态,锁定时间{}分钟]",
                                    username, badCredentialsSum, lockMinutes);
                            String uiMessage = String.format("密码错误%s次，账户已锁定，请%s分钟后重试",
                                    badCredentialsSum, lockMinutes);
                            return Response.fail("A0411", uiMessage);
                        }
                    })
                    .switchIfEmpty(Mono.fromSupplier(() -> {
                        log.error("账户[{}]登录失败-[{}]-[调用处理错误凭证处理事件方法未返回数据无法确定准确返回码]",
                                username, i18nMsg);
                        return Response.fail("A0400", i18nMsg);
                    }))
                    .onErrorResume(ex -> {
                        String logMsg = String.format("账户[%s]登录失败-[%s]-[调用处理错误凭证处理事件方法失败无法确定准确返回码]",
                                username, i18nMsg);
                        log.error(logMsg, ex);
                        return Mono.just(Response.fail("A0400", i18nMsg));
                    });
        }
        if (exception instanceof UsernameNotFoundException) {
            log.warn("账户[{}]登录失败-[{}]", username, i18nMsg);
            return Mono.just(Response.fail("A0401", i18nMsg));
        } else if (exception instanceof LockedException) {
            log.info("账户[{}]登录失败-[{}]", username, i18nMsg);
            return Mono.just(Response.fail("A0402", i18nMsg));
        } else if (exception instanceof AccountExpiredException) {
            log.warn("账户[{}]登录失败-[{}]", username, i18nMsg);
            return Mono.just(Response.fail("A0403", i18nMsg));
        } else if (exception instanceof DefaultPasswordException) {
            log.info("账户[{}]登录失败-[{}]", username, "无法使用默认密码登录，请修改密码");
            return Mono.just(Response.fail("A0412", i18nMsg));
        }
        log.error("账户[{}]登录失败-[{}]", username, i18nMsg);
        return Mono.just(Response.fail("A0400", i18nMsg));
    }

}
