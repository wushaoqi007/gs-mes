package com.greenstone.mes.table.adapter;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;

import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CacheUtil {

    private static final Cache<String, TimedCache> cacheMap = cn.hutool.cache.CacheUtil.newTimedCache(0);

    public static <K, R> R get(String cacheName, K key, Function<K, R> function) {
        TimedCache<K, R> timedCache = doubleCheckLock(cacheMap, cacheName, s -> cn.hutool.cache.CacheUtil.newTimedCache(60 * 60 * 1000));
        return doubleCheckLock(timedCache, key, function);
    }

    public static <K, R> R get(String cacheName, K key, Function<K, R> function, long timeout) {
        TimedCache<K, R> timedCache = doubleCheckLock(cacheMap, cacheName, s -> cn.hutool.cache.CacheUtil.newTimedCache(timeout));
        return doubleCheckLock(timedCache, key, function);
    }

    public static <K, R> R doubleCheckLock(Cache<K, R> cache, K key, Function<K, R> function) {
        R r = cache.get(key);
        if (r == null) {
            synchronized (CacheUtil.class) {
                r = cache.get(key);
                if (r == null) {
                    r = function.apply(key);
                    cache.put(key, r);
                }
            }
        }
        return r;
    }

}
