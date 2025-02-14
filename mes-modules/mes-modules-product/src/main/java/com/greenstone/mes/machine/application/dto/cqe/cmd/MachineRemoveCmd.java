package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MachineRemoveCmd {

    @NotEmpty(message = "请选择单据")
    private List<@NotBlank(message = "单号错误") String> serialNos;

}
