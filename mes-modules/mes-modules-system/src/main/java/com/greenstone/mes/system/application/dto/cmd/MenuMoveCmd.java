package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MenuMoveCmd {

    private String parentMenuId;

    @NotBlank(message = "缺少菜单信息")
    private String menuId;

    @NotBlank(message = "缺少排序信息")
    private Integer orderNum;

}
