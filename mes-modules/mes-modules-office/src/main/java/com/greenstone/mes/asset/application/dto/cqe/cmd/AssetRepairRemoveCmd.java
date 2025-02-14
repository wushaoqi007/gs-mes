package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AssetRepairRemoveCmd {

    @NotEmpty(message = "请选择单据")
    private List<@NotBlank(message = "单号错误") String> serialNos;

}
