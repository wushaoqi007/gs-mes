package com.greenstone.mes.form.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDataStatusChangeCmd {

    @NotBlank(message = "缺少需要变更状态的流水号")
    private String serialNo;

    @NotNull(message = "缺少变更后的状态")
    private ProcessStatus status;

}
