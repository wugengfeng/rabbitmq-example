package com.wgf.producer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SequenceUtils {
    @Value("${redis.sequence-key}")
    private String sequenceKey;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取全局id
     *
     * @return
     */
    public Long getNextId() {
        return redisTemplate.opsForValue().increment(sequenceKey);
    }
}
