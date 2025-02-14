package com.greenstone.mes.machine.application.dto.cqe.cmd;

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
public class MachineStatusChangeCmd {

    @NotNull(message = "请选择更新后的状态")
    private ProcessStatus status;

    @NotEmpty(message = "请选择单据")
    private List<@NotBlank(message = "单号错误") String> serialNos;

}
