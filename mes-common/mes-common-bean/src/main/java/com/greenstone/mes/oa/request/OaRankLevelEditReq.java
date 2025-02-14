package com.greenstone.mes.oa.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OaRankLevelEditReq {


    /**
     * 职级id
     */
    @NotNull(message = "id不为空")
    private Long id;

    /**
     * 职级名称
     */
    @NotBlank
    private String rankName;

    /**
     * 类型
     */
    @NotNull
    private Integer type;

    /**
     * 等级
     */
    @NotBlank
    private String level;

    /**
     * 部门ID
     */
    @NotNull
    private Long deptId;

    /**
     * 排序
     */
    @NotNull
    private Integer orderNum;

}
