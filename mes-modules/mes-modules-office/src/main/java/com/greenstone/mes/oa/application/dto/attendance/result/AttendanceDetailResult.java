package com.greenstone.mes.oa.application.dto.attendance.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDetailResult {

    @Excel(name = "序号")
    private Integer number;

    @Excel(name = "部门")
    private String deptName;

    @Excel(name = "人员")
    private String name;

    @Excel(name = "工号")
    private String gongHao;

    @Excel(name = "打卡日期")
    private String checkInDate;

    @Excel(name = "打卡时间")
    private String checkInTime;

    @Excel(name = "打卡地点")
    private String checkInLocation;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "打卡次数")
    private String checkInTimes;

    @Excel(name = "早晚班")
    private String shiftName;

    @Excel(name = "特殊班次")
    private String customShift;

    @JsonFormat(pattern = "HH:mm")
    @Excel(name = "标准打卡时间")
    private String schTime;

    @Excel(name = "打卡类型")
    private String checkinType;

    /**
     * 打卡当天0点（用于排序：一天的打卡放一起）
     */
    private Long dayBeginTime;

    /**
     * 打卡时间（用于排序：某天的打卡先后顺序）
     */
    private Long checkinTimeL;
}
