package com.greenstone.mes.system.application.dto.result;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import lombok.Data;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-10-18-10:34
 */
@Data
public class NavigationTree {

    @TreeId
    private Long id;

    @TreeParentId
    private Long parentId;

    private String name;

    private String category;

    private String navigationType;

    private Boolean active;

    private Boolean visible;

    private Boolean cacheable;

    private Boolean openInNewtab;

    private Boolean showNavigation;

    private String icon;

    private Long functionId;

    private String link;

    private String queryParam;

    private Integer orderNum;

    @TreeChildren
    private List<NavigationTree> children;
}
