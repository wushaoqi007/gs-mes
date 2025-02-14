package com.greenstone.mes.system.application.dto.result;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import lombok.Data;

import java.util.List;

@Data
public class PermTree {

    @TreeId
    private Long permId;

    private String permCode;

    private String permName;

    @TreeParentId
    private Long parentId;

    private String permType;

    private Integer orderNum;

    @TreeChildren
    private List<PermTree> children;
}
