package com.greenstone.mes.machine.dto.cmd;

import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.enums.MachineType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineSignFinishCmd {
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    @NotEmpty(message = "审批编号不为空")
    private String spNo;
    private MachineType machineType;
    private ProcessStatus status;
}
