package com.greenstone.mes.common.utils.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelDictType {

    String value() default "";

}
