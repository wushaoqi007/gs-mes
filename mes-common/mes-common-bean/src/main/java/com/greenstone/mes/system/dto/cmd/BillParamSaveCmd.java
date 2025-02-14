package com.greenstone.mes.system.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillParamSaveCmd {

    @NotEmpty(message = "表单类型不能为空")
    private String billType;

    @NotEmpty(message = "参数标识不能为空")
    private String paramKey;

    @NotEmpty(message = "参数值不能为空")
    private String paramValue;

}
