package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AssetClearRestoreCmd {

    @NotEmpty(message = "请选择清理单")
    private List<String> serialNos;

}
