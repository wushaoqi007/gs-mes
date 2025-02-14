package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2023/2/7 15:50
 */
@Data
public class AssetSpecInsertCmd {

    @NotEmpty(message = "请选择资产分类")
    private String typeCode;

    @NotEmpty(message = "请填写资产名称")
    private String templateName;

    @NotEmpty(message = "请填写规格")
    private String specification;

    private String unit;

}
