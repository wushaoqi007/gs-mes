package com.greenstone.mes.system.infrastructure.util;

import com.greenstone.mes.common.core.constant.Constants;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.redis.service.RedisService;
import com.greenstone.mes.system.domain.entity.ParamData;

import java.util.Collection;
import java.util.List;

/**
 * 系统参数工具类
 *
 * @author ruoyi
 */
public class ParamUtils {
    /**
     * 设置系统参数缓存
     *
     * @param key          参数键
     * @param paramData 系统参数数据列表
     */
    public static void setParamCache(String key, List<ParamData> paramData) {
        SpringUtils.getBean(RedisService.class).setCacheObject(getCacheKey(key), paramData);
    }

    /**
     * 获取系统参数缓存
     *
     * @param key 参数键
     * @return paramDetails 系统参数数据列表
     */
    public static List<ParamData> getParamCache(String key) {
        Object cacheObj = SpringUtils.getBean(RedisService.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj)) {
            List<ParamData> paramData = StringUtils.cast(cacheObj);
            return paramData;
        }
        return null;
    }

    /**
     * 删除指定系统参数缓存
     *
     * @param key 系统参数键
     */
    public static void removeParamCache(String key) {
        SpringUtils.getBean(RedisService.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空系统参数缓存
     */
    public static void clearParamCache() {
        Collection<String> keys = SpringUtils.getBean(RedisService.class).keys(Constants.SYS_PARAM_KEY + "*");
        SpringUtils.getBean(RedisService.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.SYS_PARAM_KEY + configKey;
    }
}
