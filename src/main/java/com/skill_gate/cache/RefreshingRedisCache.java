package com.skill_gate.cache;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.lang.NonNull;

import java.time.Duration;

public class RefreshingRedisCache extends RedisCache {

    private final RedisOperations<String, Object> redisOperations;
    private final Duration ttl;

    public RefreshingRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig,
                                RedisOperations<String, Object> redisOperations, Duration ttl) {
        super(name, cacheWriter, cacheConfig);
        this.redisOperations = redisOperations;
        this.ttl = ttl;
    }

    @Override
    public ValueWrapper get(@NonNull Object key) {
        ValueWrapper valueWrapper = super.get(key);
        if (valueWrapper != null) {
            redisOperations.expire(getName() + "::" + key, ttl);
        }
        return valueWrapper;
    }
}
