package com.greenstone.mes.table;

import java.lang.annotation.*;

/**
 * 业务表字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WorkflowField {

    /**
     * @return 流程字段名称
     */
    String value() default "";


    FlowFieldType fieldType() default FlowFieldType.NORMAL;
}
