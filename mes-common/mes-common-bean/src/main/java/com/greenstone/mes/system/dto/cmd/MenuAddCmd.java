package com.greenstone.mes.system.dto.cmd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuAddCmd {

    private String menuId;

    @NotBlank(message = "请填写菜单名称")
    private String menuName;

    /**
     * 菜单类型
     */
    @NotNull(message = "请选择菜单类型")
    private Integer menuType;

    /**
     * 父菜单ID
     */
    private String parentId;

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
    @JsonProperty(value = "isFrame")
    private Boolean frame = false;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    @JsonProperty(value = "isCache")
    private Boolean cacheable = true;

    /**
     * 显示状态（1显示 0隐藏）
     */
    @JsonProperty(value = "isVisible")
    private Boolean visible = true;

    /**
     * 菜单状态（1气筒 0停用）
     */
    @JsonProperty(value = "isActive")
    private Boolean activable = true;

    /**
     * 菜单图标
     */
    private String icon;

    private String dataTableName;

    private Boolean usingProcess;

}
