package com.greenstone.mes.system.application.dto.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2023/3/6 9:35
 */
@Data
public class FormQuery {

    @NotEmpty(message = "请选择菜单")
    private String menuId;

}
