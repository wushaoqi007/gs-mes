package com.greenstone.mes.system.dto.result;

import lombok.Data;

@Data
public class MenuFormResult {

    private String formId;

    private String formName;

    private String menuType;

    private String icon;

    private Boolean usingProcess;

    private String defaultJson;

    private String customJson;

}
