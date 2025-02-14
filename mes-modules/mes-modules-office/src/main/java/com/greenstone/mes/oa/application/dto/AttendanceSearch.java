package com.greenstone.mes.oa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSearch {

    /**
     * 姓名
     */
    private String userName;

    /**
     * 查询考勤开始时间字符串
     */
    private String startDate;

    /**
     * 查询考勤结束时间字符串
     */
    private String endDate;

    /**
     * 查询月份
     */
    private String month;

    /**
     * id
     */
    private String userId;

    /**
     * 电话
     */
    private String tel;

    /**
     * 部门
     */
    private String deptName;
    /**
     * 部门id
     */
    private String deptId;


}
