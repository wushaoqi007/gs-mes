package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:46
 */
@Data
public class RoleRemoveCmd {

    @NotNull(message = "请选择是否保留成员权限")
    private Boolean keepMemberPerm;
}
