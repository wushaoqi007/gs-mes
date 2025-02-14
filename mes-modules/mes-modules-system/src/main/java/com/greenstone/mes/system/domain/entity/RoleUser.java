package com.greenstone.mes.system.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wushaoqi
 * @date 2024-10-21-10:12
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RoleUser {
    private Long roleId;
    private Long userId;
}
