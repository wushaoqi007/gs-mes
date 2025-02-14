package com.greenstone.mes.table.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * 业务表字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamField {

    /**
     * @return 字段名称
     */
    String value();

    /**
     * @return 修改时是否记录
     */
    boolean recordIfChange() default false;

    /**
     * @return 修改时是否需要原因
     */
    boolean reasonIfChange() default false;
}
