package com.vividh.url_shortner.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int LIMIT = 10;
    private static final int WINDOW = 60000;

    RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String ipAddress) {
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        String value = UUID.randomUUID().toString();
        String key = "rate_limit:" + ipAddress;
        long now = System.currentTimeMillis();

        //Remove old operations
        operations.removeRangeByScore(key, 0, now - WINDOW);

        Long count = operations.zCard(key);
        if (count != null && count >= LIMIT) {
            return false;
        }

        //Add new operations
        operations.add(key, value, now);

        //Set expiration
        redisTemplate.expire(key, 1, TimeUnit.MINUTES);

        return true;
    }
}
