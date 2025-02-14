package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("sys_menu")
public class MenuPo extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型
     */
    private Integer menuType;

    private String serviceName;

    /**
     * 父菜单ID
     */
    private String parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 是否为外链（1是 0否）
     */
    @TableField("is_frame")
    private Boolean frame;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    @TableField("is_cache")
    private Boolean cacheable;

    /**
     * 显示状态（1显示 0隐藏）
     */
    @TableField("is_visible")
    private Boolean visible;

    /**
     * 菜单状态（1启用 0禁用）
     */
    @TableField("is_active")
    private Boolean activable;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 数据表名称
     */
    private String dataTableName;

    private String defaultJson;

    private String customJson;

    private String fieldsJson;

    private Boolean usingProcess;

    private String processDefinitionId;

}
