package org.lzx.web.gateway.infrastructure.config.spring.security;

import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.BadCredentialsStatusReactiveUserDetailsService;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.PostUserDetailsChecker;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.upstream.AuthenticationTokenUpStreamFilter;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.userdetails.BadCredentialsStatusReactiveUserDetailsServiceImpl;
import org.lzx.web.gateway.infrastructure.config.spring.security.authentication.userdetails.manager.RemoteUserDetailsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

/**
 * @author LZx
 * @since 2021/12/22
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AuthenticationConfig {

    @Value("${load-balance-scheme:lb}")
    private String loadBalanceScheme;

    /**
     * 因为外部请求都会被鉴权，网关内的所有请求只应该查询已注册服务或者直接请求应用服务器，不能创建请求应用自身的请求。
     * 由于用户详情服务一定在网关路由清单内，获取其路由配置来发起用户详情请求，这样便于配置的统一维护
     *
     * @param builder                   响应式请求客户端
     * @param routeLocator              已注册的路由清单
     * @param userDetailsServiceRouteId 用户详情服务对应的路由ID
     * @param filter                    负载均衡过滤器，当相关路由的URI标识为负载均衡时将使用该过滤器来构建{@link WebClient}
     */
    @Bean
    @RefreshScope
    BadCredentialsStatusReactiveUserDetailsService userDetailsService(
            WebClient.Builder builder,
            RouteLocator routeLocator,
            @Value("${spring.cloud.gateway.security.user-details-service-route-id}") String userDetailsServiceRouteId,
            @Qualifier("loadBalancerExchangeFilterFunction") LoadBalancedExchangeFilterFunction filter) {
        log.info("创建用户详情服务类实例-[0]-开始");
        return routeLocator.getRoutes()
                .filter(route -> route.getId().equals(userDetailsServiceRouteId))
                .next()
                .switchIfEmpty(Mono.error(() -> {
                    String msg = String.format("用户详情接口所在服务的RouteId[%s]不存在", userDetailsServiceRouteId);
                    return new IllegalArgumentException(msg);
                }))
                .map(route -> {
                    URI uri = route.getUri();
                    WebClient webClient = builder
                            .filters(filterFunctions -> {
                                if (loadBalanceScheme.equals(uri.getScheme())) {
                                    filterFunctions.add(filter);
                                }
                            })
                            .baseUrl(uri.toASCIIString())
                            .build();
                    RemoteUserDetailsClient client = new RemoteUserDetailsClient(webClient);
                    return new BadCredentialsStatusReactiveUserDetailsServiceImpl(client);
                })
                .doOnSuccess(ud -> log.info("创建用户详情服务类实例-[1]-成功"))
                .doOnError(throwable -> log.error("创建用户详情服务类实例-[1]-失败-[" + throwable.getMessage() + "]"))
                .publishOn(Schedulers.boundedElastic())
                .block();
    }

    @Bean
    @Primary
    ReactiveAuthenticationManager authenticationManager(BadCredentialsStatusReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService) {

                    @Override
                    public void setMessageSource(MessageSource messageSource) {
                        // Spring BUG, ApplicationContextAwareProcessor会给所有MessageSourceAware实例设置MessageSource
                        super.setMessageSource(new SpringSecurityMessageSource());
                    }

                };
        /*
         * Spring 应该提供一个自定义SetPreAuthenticationChecks的方法
         * 有这样一种场景：系统可以表单登录，也可以从其它渠道用自定义的方式登录本系统，当开发人员希望只有系统自己的表单登录时，在核对密码前
         * 去检验如账户锁定一小时是否到期，如果锁定到期则更新账户状态，***开发需要这个自由度***
         * 没有这个自定义PreAuthenticationChecks方法时，开发人员只能在UserDetailsServer#findByUsername(String)内检查并解锁账户
         * 但是，不同方式的登录都会调用该方法，这样就无法实现只有本系统自身的表单登录操作才自动核对是否需要并解锁账户
         */
        manager.setPostAuthenticationChecks(new PostUserDetailsChecker());
        return manager;
    }

    /**
     * {@link ServerHttpSecurity.RequestCacheSpec}内的默认请求缓存，但是{@link DefaultAuthenticationSuccessHandler}内
     * 也需要使用，显式地定义以防以后以后自定义实现忘了任意一边做修改
     */
    @Bean
    ServerRequestCache serverRequestCache() {
        return new WebSessionServerRequestCache();
    }

    @Bean
    AuthenticationTokenUpStreamFilter authenticationTokenUpStreamFilter() {
        return new AuthenticationTokenUpStreamFilter();
    }

}
