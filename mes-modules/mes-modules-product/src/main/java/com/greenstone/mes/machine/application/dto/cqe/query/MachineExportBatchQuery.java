package com.greenstone.mes.machine.application.dto.cqe.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class MachineExportBatchQuery {

    @NotEmpty(message = "单号不为空")
    private List<String> serialNos;

}
