package com.greenstone.mes.oa.application.dto;


import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendCheatResult {

    @Excel(name = "打卡设备")
    private String deviceId;
    @Excel(name = "打卡日期")
    private String schCheckinTime;
    @Excel(name = "打卡时间")
    private String checkinTime;

    //    private String employeeNo;
    @Excel(name = "部门")
    private String deptName;
    @Excel(name = "姓名")
    private String userName;
    @Excel(name = "打卡地点")
    private String location;
    @Excel(name = "备注")
    private String remark;

}

