package com.greenstone.mes.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private RedisService redisService;

    public synchronized boolean lock(String key, long timeout) {
        if (redisService.getCacheObject(key) != null) {
            return false;
        }
        redisService.setCacheObject(key, "", timeout, TimeUnit.SECONDS);
        return true;
    }

    public boolean unlock(String key) {
        return redisService.deleteObject(key);
    }

}
