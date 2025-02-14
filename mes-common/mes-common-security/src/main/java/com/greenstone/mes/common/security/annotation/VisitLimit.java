package com.greenstone.mes.common.security.annotation;

import java.lang.annotation.*;

/**
 * 接口限流：同一个ip默认3秒内只能点击访问1次接口
 */
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitLimit {
    //标识 指定sec时间段内的访问次数限制
    int limit() default 1;

    //标识 时间段
    long sec() default 3;
}
