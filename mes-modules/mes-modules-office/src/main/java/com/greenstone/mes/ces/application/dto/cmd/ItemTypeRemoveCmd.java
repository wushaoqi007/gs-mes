package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author wushaoqi
 * @date 2023-05-22-13:47
 */
@Data
public class ItemTypeRemoveCmd {
    @NotEmpty(message = "请选择物品分类")
    private String typeCode;
}
