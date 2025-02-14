package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParamData {

    @TreeId
    private String id;
    @TreeParentId
    private String parentId;
    private String paramType;
    private String paramValue1;
    private String paramValue2;
    private String paramValue3;
    private Integer orderNum;
    private String remark;
    @TreeChildren
    private List<ParamData> children;

}
