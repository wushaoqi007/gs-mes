package com.greenstone.mes.oa.domain.entity;


import com.greenstone.mes.oa.enums.DormMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DormMember {
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
    private Integer bedNo;
    /**
     * 员工id
     */
    private Long employeeId;
    /**
     * 员工姓名
     */
    private String employeeName;
    /**
     * 状态
     */
    private DormMemberStatus status;
    /**
     * 入住时间
     */
    private LocalDateTime inTime;
    /**
     * 本人电话
     */
    private String telephone;
    /**
     * 紧急电话
     */
    private String urgentTel;


}