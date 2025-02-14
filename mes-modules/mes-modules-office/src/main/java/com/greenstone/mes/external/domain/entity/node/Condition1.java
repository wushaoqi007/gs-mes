package com.greenstone.mes.external.domain.entity.node;

import com.greenstone.mes.system.enums.FormDataType;
import com.greenstone.mes.system.enums.FormFieldMatchAction;
import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/2/24 14:04
 */
@Data
public class Condition1 {

    private String fieldCode;

    private FormDataType fieldType;

    private FormFieldMatchAction matchAction;

    private Object matchValue;

}
