package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CesClearRemoveCmd {

    @NotNull(message = "请选择清理单")
    private List<String> serialNos;

}
