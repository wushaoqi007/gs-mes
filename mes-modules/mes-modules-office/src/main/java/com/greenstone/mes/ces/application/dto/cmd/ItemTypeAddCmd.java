package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-05-22-13:43
 */
@Data
public class ItemTypeAddCmd {
    private Long id;

    @NotEmpty(message = "分类编码不能为空")
    private String typeCode;

    @NotEmpty(message = "分类名称不能为空")
    private String typeName;

    private String parentTypeCode;
}
