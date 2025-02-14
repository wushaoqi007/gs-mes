package com.greenstone.mes.system.application.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import com.greenstone.mes.system.domain.entity.Function;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class MenuTree implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TreeId
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    private String category;

    /**
     * 菜单类型
     */
    private String menuType;

    /**
     * 父菜单ID
     */
    @TreeParentId
    private Long parentId;

    private String component;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    private String link;

    private String queryParam;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    @JsonProperty("isCache")
    private boolean cacheable;

    /**
     * 显示状态（1显示 0隐藏）
     */
    @JsonProperty("isVisible")
    private boolean visible;

    /**
     * 菜单状态（1启用 0停用）
     */
    @JsonProperty("isActive")
    private boolean active;

    @JsonProperty("isOpenInNewtab")
    private boolean openInNewtab;

    @JsonProperty("isShowNavigation")
    private boolean showNavigation;

    /**
     * 菜单图标
     */
    private String icon;

    @TreeChildren
    private List<MenuTree> children;

    private Function function;

}
