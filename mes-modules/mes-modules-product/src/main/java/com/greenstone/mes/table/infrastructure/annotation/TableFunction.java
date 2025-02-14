package com.greenstone.mes.table.infrastructure.annotation;

import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;

import java.lang.annotation.*;

/**
 * 业务表字段注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableFunction {

    /**
     * @return 功能id
     */
    String id() default "";

    /**
     * @return domain对象
     */
    Class<?> entityClass();

    /**
     * @return 持久化对象
     */
    Class<?> poClass();

    /**
     * @return 更新时原因策略，默认有就保存，如果选择了 IF_EXIST 或 NECESSARY 则必须要指定 reasonClass()
     */
    UpdateReason updateReason() default UpdateReason.IF_EXIST;

    /**
     * @return 更新时原因对象
     */
    Class<?> reasonClass() default TableChangeReason.class;
}
