package com.greenstone.mes.table.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * 业务表字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReasonField {

    /**
     * @return 原因名称
     */
    String value();

    /**
     * @return 是否必须
     */
    boolean necessary() default false;
}
