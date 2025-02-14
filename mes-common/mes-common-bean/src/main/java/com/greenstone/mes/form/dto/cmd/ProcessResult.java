package com.greenstone.mes.form.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessResult {

    @NotBlank(message = "请指定表单类型")
    private String formId;

    @NotBlank(message = "请选择单据")
    private String serialNo;

    @NotNull(message = "缺少审批后的状态")
    private ProcessStatus status;

    private String remark;

    private boolean end;

}
