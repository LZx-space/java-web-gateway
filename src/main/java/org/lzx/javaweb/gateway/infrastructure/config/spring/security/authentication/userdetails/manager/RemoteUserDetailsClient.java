package org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.manager;

import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.BadCredentialsStatusDto;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.SimpleGrantedAuthorityDto;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authentication.userdetails.dto.UserDto;
import org.lzx.javaweb.gateway.infrastructure.config.spring.security.authorization.ApiGrantedAuthority;
import org.lzx.javaweb.gateway.infrastructure.util.JsonUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author LZx
 * @since 2021/12/12
 */
@Slf4j
public class RemoteUserDetailsClient {

    private final WebClient webClient;

    public RemoteUserDetailsClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 通过用户名查询用户
     *
     * @param username 用户名
     * @return 用户的JSON数据
     */
    public Mono<UserDto> findByUsername(String username) {
        return webClient.get().uri("/users/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    log.error("调用用户服务失败[{}]", clientResponse.rawStatusCode());
                    return Mono.error(new InternalAuthenticationServiceException("调用用户服务获取用户信息失败"));
                })
                .bodyToMono(String.class)
                .flatMap(json -> {
                    if (!StringUtils.hasText(json)) {
                        log.info("用户[{}]不存在", username);
                        return Mono.error(new UsernameNotFoundException("用户不存在"));
                    }
                    return Mono.fromSupplier(() -> {
                        try {
                            UserDto userDto = JsonUtils.read(json, UserDto.class);
                            Set<GrantedAuthority> grantedAuthorities = parseAuthorities(json);
                            userDto.setAuthorities(grantedAuthorities);
                            return userDto;
                        } catch (Exception ex) {
                            log.error("反序列化用户服务信息失败", ex);
                            throw new InternalAuthenticationServiceException("反序列化用户信息失败");
                        }
                    });
                });
    }

    /**
     * 处理错误凭证事件
     *
     * @param username 用户名
     * @return 错误凭证相关的账户状态数据
     */
    public Mono<BadCredentialsStatusDto> handleBadCredentialsEvent(String username) {
        return webClient.put().uri("/bad_credentials_events/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(BadCredentialsStatusDto.class);
    }

    /**
     * GrantedAuthority目前认证后有多种实例，反序列化特别处理
     *
     * @param json 权限集合对应的JSON
     * @return 权限实例集
     * @throws IOException 反序列化异常
     */
    private Set<GrantedAuthority> parseAuthorities(String json) throws IOException {
        Set<GrantedAuthority> result = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectReader objectReader = objectMapper.reader();
        JsonNode jsonNode = objectReader.readTree(json);
        JsonNode authorities = jsonNode.path("authorities");
        Iterator<JsonNode> elements = authorities.elements();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            if (next.has("permittedApis")) {
                ApiGrantedAuthority apiGrantedAuthority = objectReader.readValue(next, ApiGrantedAuthority.class);
                result.add(apiGrantedAuthority);
            } else {
                SimpleGrantedAuthorityDto simpleGrantedAuthority = objectReader.readValue(next, SimpleGrantedAuthorityDto.class);
                result.add(simpleGrantedAuthority);
            }
        }
        return result;
    }

}
