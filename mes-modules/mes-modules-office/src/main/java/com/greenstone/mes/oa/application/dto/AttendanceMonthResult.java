package com.greenstone.mes.oa.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考勤月数据
 *
 * @author wushaoqi
 * @date 2022-04-19-15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMonthResult {
    /**
     * 姓名
     */
    private String userName;
    /**
     * 部门
     */
    private String deptName;
    /**
     * id
     */
    private String userId;


    /**
     * 考勤集合
     */
    private List<AttendanceResultDTO> resultsList;
}
