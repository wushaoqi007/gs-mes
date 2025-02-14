package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineCalculateDetailEditCmd {
    @NotEmpty(message = "核价单号不为空")
    private String calculateSerialNo;
    @NotEmpty(message = "详情id不为空")
    private String calculateDetailId;
    @NotNull(message = "核算价格不为空")
    private Double calculatePrice;
    @NotEmpty(message = "核算参数不为空")
    private String calculateJson;
}
