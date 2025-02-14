package com.greenstone.mes.oa.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OaRankLevelUserRelationListResp {

    private Long id;

    private Long rankId;

    private String rankName;

    private String userName;

    private String userId;

    private String level;

    private Long deptId;

    /**
     * 评级时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date gradeTime;

}
