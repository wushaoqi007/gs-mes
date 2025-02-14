package com.greenstone.mes.system.application.dto.result;

import com.greenstone.mes.system.enums.FormDataType;
import lombok.Data;

/**
 * @author gu_renkai
 * @date 2023/3/6 9:38
 */
@Data
public class FormFieldResult {

    private String id;
    private String formId;
    private Boolean customDefault;
    private String fieldKey;
    private String fieldLabel;
    private FormDataType dataType;
    private String tableField;

}
