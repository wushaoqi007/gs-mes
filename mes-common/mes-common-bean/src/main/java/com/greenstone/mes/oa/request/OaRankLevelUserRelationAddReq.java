package com.greenstone.mes.oa.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OaRankLevelUserRelationAddReq {

    @Valid
    private List<Relation> list;


    @Data
    public static class Relation {

        /**
         * 职级ID
         */
        @NotNull(message = "职级ID不为空")
        private Long rankId;

        /**
         * 人员ID
         */
        @NotNull(message = "人员ID不为空")
        private Long userId;
    }

}
