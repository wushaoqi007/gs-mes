package com.greenstone.mes.common.redis.manager;

import com.greenstone.mes.common.core.utils.StringUtils;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * @author wushaoqi
 * @date 2023-02-17-8:22
 */
public class CustomRedisCacheManager extends RedisCacheManager {
    public CustomRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 重写createRedisCache方法
     *
     * @param name 原来的name只是作为redis存储键名
     *             重写的name可通过"#"拼接过期时间：
     *             1. 如果没有"#"则默认不设置过期时间
     *             2. 拼接的第一个"#"后面为过期时间，第二个"#"后面为时间单位
     *             3. 时间单位的表示使用: d(天)、h(小时)、m(分钟)、s(秒), 默认为h(小时)
     */
    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        // 解析name,设置过期时间
        if (StringUtils.isNotEmpty(name) && name.contains("#")) {
            String[] split = name.split("#");

            // 缓存键名
            String cacheName = split[0];
            // "#"后第一位是时间
            int expire = Integer.parseInt(split[1]);
            // 过期时间，默认为h(小时)
            Duration duration = Duration.ofHours(expire);
            // 根据"#"后第二位字符判断过期时间的单位，设置相应的过期时间，默认时间单位是h(小时)
            if (split.length == 3) {
                duration = switch (split[2]) {
                    case "d" -> Duration.ofDays(expire);
                    case "m" -> Duration.ofMinutes(expire);
                    case "s" -> Duration.ofSeconds(expire);
                    default -> Duration.ofHours(expire);
                };
            }
            return super.createRedisCache(cacheName, cacheConfig.entryTtl(duration));
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
