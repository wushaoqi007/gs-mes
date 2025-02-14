package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2023/1/30 15:06
 */
@Data
public class AssetTypeDeleteCmd {

    @NotEmpty(message = "请选择资产分类")
    private String typeCode;

}
