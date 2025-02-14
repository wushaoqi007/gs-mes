package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CesReturnRemoveCmd {

    @NotNull(message = "请选择归还单")
    private List<String> serialNos;

}
