package com.greenstone.mes.system.application.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:46
 */
@Data
public class RoleAllocateUsersCmd {

    private Long roleId;

    @NotEmpty(message = "请选择用户")
    private List<Long> userIds;
}
