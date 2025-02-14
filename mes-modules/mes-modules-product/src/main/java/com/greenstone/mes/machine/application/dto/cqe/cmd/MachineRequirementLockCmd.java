package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirementLockCmd {

    @NotEmpty(message = "请选择单据")
    private List<@NotBlank(message = "单号错误") String> serialNos;

}
