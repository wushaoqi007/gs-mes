package com.greenstone.mes.oa.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OaRankLevelUserRelationListReq {

    /**
     * 职级名称
     */
    private String rankName;

    /**
     * 姓名
     */
    private String userName;


    /**
     * 部门id
     */
    @NotNull(message = "部门Id不为空")
    private Long deptId;


}
