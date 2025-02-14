package com.greenstone.mes.common.log.aspect;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.common.log.annotation.ApiLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;

/**
 * 操作日志记录处理
 *
 * @author ruoyi
 */
@Slf4j
@Aspect
@Component
public class ApiLogAspect {

    /**
     * 请求处理前执行
     */
    @Before("@annotation(apiLog)")
    public void doBeforeProcess(JoinPoint joinPoint, ApiLog apiLog) {
        String prefix = joinPoint.getTarget().getClass().getSimpleName() + "." + joinPoint.getSignature().getName();
        log.info("[{}] received request.", prefix);

        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        if (args != null && parameterNames != null) {
            if (parameterNames.length == args.length) {
                for (int i = 0; i < args.length; i++) {
                    log.info("[{}], [{}]: {}", prefix, parameterNames[i], args[i] instanceof InputStreamSource ? "this arg is a file." :
                            JSON.toJSONString(args[i]));
                }
            } else {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof InputStreamSource) {
                        log.info("[{}]: {}", prefix, " this arg is a file.");
                    } else {
                        log.info("[{}]: {}", prefix, JSON.toJSONString(args[i]));
                    }

                }
            }
        }
    }


}
