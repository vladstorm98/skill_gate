package com.skill_gate.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skill_gate.entity.DbUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class DraftStore {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofHours(36);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public <T> T get(String prefix, DbUser admin, Class<T> type) {
        var key = prefix + admin.getId();
        var json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("Corrupt draft value for key '{}', removing it: {}", key, e.getMessage());
            redisTemplate.delete(key);
            return null;
        }
    }

    public void save(String prefix, DbUser admin, Object draft) {
        var key = prefix + admin.getId();

        try {
            var json = objectMapper.writeValueAsString(draft);
            redisTemplate.opsForValue().set(key, json, TTL);
        } catch (Exception e) {
            log.error("Failed to serialize draft for key '{}'", key, e);
        }
    }

    public void remove(String prefix, DbUser admin) {
        redisTemplate.delete(prefix + admin.getId());
    }
}
