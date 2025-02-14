package com.greenstone.mes.common.log.annotation;

import java.lang.annotation.*;

/**
 * 接口打印参数日志
 *
 * @author gu_renkai
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {

}
