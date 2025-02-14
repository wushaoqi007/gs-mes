package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RequisitionRemoveCmd {

    @NotNull(message = "请选择领用单")
    private List<String> serialNos;

}
