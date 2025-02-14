package com.greenstone.mes.oa.domain.entity;


import com.greenstone.mes.oa.enums.DormMemberOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DormRecord {
    /**
     * id
     */
    private Long id;
    /**
     * 宿舍编号
     */
    private String dormNo;
    /**
     * 床位号
     */
    private String bedNo;
    /**
     * 员工id
     */
    private Long employeeId;
    /**
     * 员工姓名
     */
    private String employeeName;
    /**
     * 动作
     */
    private DormMemberOperation operation;
    /**
     * 入住时间
     */
    private LocalDateTime time;

}