package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
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
@TableName(value = "sys_role_new")
public class RoleDO extends BaseEntity {
    private Long roleId;
    private String roleName;
    private Integer userNum;
}
