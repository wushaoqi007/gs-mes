package com.greenstone.mes.common.core.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认7（天）
     */
    public final static long EXPIRATION = 7 * 24 * 60;

    /**
     * 缓存刷新时间，默认3（天）
     */
    public final static long REFRESH_TIME = 3 * 24 * 60;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";
}
