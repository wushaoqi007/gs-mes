package com.greenstone.mes.oa.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OaRankLevelUserRelationEditReq {

    /**
     * ID
     */
    @NotNull(message = "ID不为空")
    private Long id;

    /**
     * 用户
     */
    @NotNull(message = "用户id不为空")
    private Long userId;

    /**
     * 职级ID
     */
    @NotNull(message = "职级ID不为空")
    private Long rankId;


}
