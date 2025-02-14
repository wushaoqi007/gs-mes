package com.greenstone.mes.ces.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2023-05-22-15:23
 */
@Data
public class ItemSpecRemoveCmd {
    @NotNull(message = "请选择型号规格")
    private Long id;
}
