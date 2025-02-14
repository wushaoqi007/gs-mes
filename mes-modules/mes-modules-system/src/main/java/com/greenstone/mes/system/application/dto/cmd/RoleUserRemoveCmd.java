package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:46
 */
@Data
public class RoleUserRemoveCmd {

    @NotNull(message = "请指定取消用户授权的角色")
    private Long roleId;

    private Long userId;
}
