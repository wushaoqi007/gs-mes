package com.greenstone.mes.oa.dto.result;


import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DormRecordResult {
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

    private Long employeeId;

    private String employeeName;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH")
    private LocalDateTime time;
    /**
     * 床位号
     */
    private Integer bedNo;

    /**
     * 宿舍操作
     */
    private DormMemberOperation operation;


}