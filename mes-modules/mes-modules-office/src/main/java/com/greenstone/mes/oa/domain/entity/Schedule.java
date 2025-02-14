package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排班：用于判断是否工作日（因为企业微信不能更改历史排班，所以不能用于计算）
 *
 * @author gu_renkai
 * @date 2022/11/23 15:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    private CpId cpId;

    private WxUserId userId;

    private Integer groupId;

    private String groupName;

    private String ymd;

    private Integer scheduleId;

    private String scheduleName;

    private Integer workSec;

    private Integer offWorkSec;

    public TimeSection getWorkSection(long dayBeginTime) {
        return new TimeSection(dayBeginTime + workSec, dayBeginTime + offWorkSec);
    }
}
