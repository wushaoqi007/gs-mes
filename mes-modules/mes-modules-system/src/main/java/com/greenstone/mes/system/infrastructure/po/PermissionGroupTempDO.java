package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.greenstone.mes.system.domain.Condition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-22-9:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "sys_permission_group_temp", autoResultMap = true)
public class PermissionGroupTempDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String typeName;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> rights;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Condition> viewFilter;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Condition> updateFilter;

    private Boolean pagePermission;


}
