package com.greenstone.mes.machine.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MachineProviderRemoveCmd {

    @NotEmpty(message = "请选择供应商")
    private List<@NotBlank(message = "id不为空") String> ids;

}
