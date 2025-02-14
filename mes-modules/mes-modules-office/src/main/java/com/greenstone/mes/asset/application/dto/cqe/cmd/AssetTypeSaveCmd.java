package com.greenstone.mes.asset.application.dto.cqe.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2023/1/30 15:06
 */
@Data
public class AssetTypeSaveCmd {

    private Long id;

    @NotEmpty(message = "分类编码不能为空")
    private String typeCode;

    @NotEmpty(message = "分类名称不能为空")
    private String typeName;

    private String parentTypeCode;

}
