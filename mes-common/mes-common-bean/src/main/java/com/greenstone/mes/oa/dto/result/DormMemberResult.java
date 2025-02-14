package com.greenstone.mes.oa.dto.result;


import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DormMemberResult {
    /**
     * 城市
     */
    private String city;
    /**
     * 地址
     */
    private String address;
    /**
     * 房间号
     */
    private String roomNo;
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
     * 入住时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH")
    private LocalDateTime inTime;
    /**
     * 本人电话
     */
    private String telephone;
    /**
     * 紧急电话
     */
    private String urgentTel;
    /**
     * 状态
     */
    private DormMemberStatus status;

}