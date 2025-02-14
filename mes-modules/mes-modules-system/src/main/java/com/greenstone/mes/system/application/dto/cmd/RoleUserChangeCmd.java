package com.greenstone.mes.system.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:46
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserChangeCmd {

    private String permissionType;

    private Long roleId;

    private Long userId;
}
