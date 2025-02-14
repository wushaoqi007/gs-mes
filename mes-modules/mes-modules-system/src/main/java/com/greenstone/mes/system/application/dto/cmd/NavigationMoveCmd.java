package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NavigationMoveCmd {

    private Long parentId;

    @NotNull(message = "缺少功能信息")
    private Long id;

    @NotNull(message = "缺少排序信息")
    private Integer orderNum;

}
