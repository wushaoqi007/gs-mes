package com.greenstone.mes.oa.domain.entity;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2022/11/24 15:18
 */
@Data
public class UserCheckinOption {

    private Long groupId;

    private String groupName;

    private Integer scheduleId;

    private String scheduleName;

    private Integer workSec;

    private Integer offWorkSec;

    private Integer earliestWorkSec;

    private Integer latestOffWorkSec;

}
