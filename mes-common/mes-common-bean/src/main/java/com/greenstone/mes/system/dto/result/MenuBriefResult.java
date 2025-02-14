package com.greenstone.mes.system.dto.result;

import lombok.Data;

@Data
public class MenuBriefResult {

    private String menuId;

    private String menuName;

    private String serviceName;

    private Integer menuType;

    private String parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private boolean frame;

    private boolean cacheable;

    private boolean visible;

    private boolean activable;

    private String perms;

    private String icon;

    private boolean usingProcess;

}
