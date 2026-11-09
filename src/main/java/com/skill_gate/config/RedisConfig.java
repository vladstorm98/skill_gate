package com.skill_gate.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skill_gate.cache.RefreshingRedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.Map;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisCacheManager cacheManager(RedisTemplate<String, Object> redisTemplate,
                                          StringRedisSerializer keySerializer,
                                          GenericJackson2JsonRedisSerializer valueSerializer) {
        var writer = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(20))
                .disableCachingNullValues()
                .serializeKeysWith(fromSerializer(keySerializer))
                .serializeValuesWith(fromSerializer(valueSerializer));

        var cacheConfig = Map.of(
                "lessonCache", defaultConfig.entryTtl(Duration.ofMinutes(10)),
                "positionCache", defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        return new RedisCacheManager(writer, defaultConfig, cacheConfig) {
            @Override
            protected @NonNull Cache decorateCache(@NonNull Cache cache) {
                return new RefreshingRedisCache(cache.getName(), writer, defaultConfig, redisTemplate, Duration.ofMinutes(10));
            }
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(StringRedisSerializer keySerializer,
                                                       GenericJackson2JsonRedisSerializer valueSerializer) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        return template;
    }

    @Bean
    public StringRedisSerializer keySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer valueSerializer() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
