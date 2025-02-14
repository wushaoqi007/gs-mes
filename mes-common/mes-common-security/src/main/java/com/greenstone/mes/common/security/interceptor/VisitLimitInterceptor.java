package com.greenstone.mes.common.security.interceptor;

import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.common.core.utils.ip.IpUtils;
import com.greenstone.mes.common.redis.service.RedisService;
import com.greenstone.mes.common.security.annotation.VisitLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author wushaoqi
 * @date 2023-04-12-15:06
 */
@Slf4j
public class VisitLimitInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            if (!method.isAnnotationPresent(VisitLimit.class)) {
                return true;
            }
            VisitLimit accessLimit = method.getAnnotation(VisitLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int limit = accessLimit.limit();
            long sec = accessLimit.sec();
            String key = IpUtils.getIpAddr(request) + request.getRequestURI();
            Integer maxLimit = null;
            Object value = SpringUtils.getBean(RedisService.class).getCacheObject(key);
            if (value != null && !value.equals("")) {
                maxLimit = Integer.valueOf(String.valueOf(value));
            }
            if (maxLimit == null) {
                SpringUtils.getBean(RedisService.class).setCacheObject(key, "1", sec, TimeUnit.SECONDS);
            } else if (maxLimit < limit) {
                int i = maxLimit + 1;
                SpringUtils.getBean(RedisService.class).setCacheObject(key, Integer.toString(i), sec, TimeUnit.SECONDS);
            } else {
                log.info("ip:{} 接口限制访问,{}秒{}次", sec, limit, key);
                throw new ServiceException(SysError.E14001);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}