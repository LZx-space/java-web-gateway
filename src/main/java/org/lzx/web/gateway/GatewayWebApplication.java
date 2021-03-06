package org.lzx.web.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * @author LZx
 * @since 2021/9/26
 */
@EnableDiscoveryClient
@EnableRedisWebSession
@SpringBootApplication
@ConfigurationPropertiesScan
public class GatewayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class, args);
    }

}
