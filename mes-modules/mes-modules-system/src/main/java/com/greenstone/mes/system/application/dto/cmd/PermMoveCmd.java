package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PermMoveCmd {

    @NotNull(message = "缺少权限信息")
    private Long permId;

    @NotNull(message = "缺少排序信息")
    private Integer orderNum;

    private Long parentId;

}
