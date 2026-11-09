package com.skill_gate.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
@Profile("prod")
public class RedisClientConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer topologyRefreshCustomizer() {
        return builder -> {
            var topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enablePeriodicRefresh(Duration.ofSeconds(30))
                    .enableAllAdaptiveRefreshTriggers()
                    .build();

            var clientOptions = ClusterClientOptions.builder()
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build();

            builder.clientOptions(clientOptions);
        };
    }
}
