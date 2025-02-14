package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-11-11:27
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineCheckDetail {


    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String designer;
    private Long takeNumber;
    private Long checkedNumber;
    private Long toBeCheckedNumber;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;
    private Long receivedNumber;
    private String outWarehouseCode;
    private CheckResultType checkResultType;
    private CheckResult checkResult;
    private String ngType;
    private String subNgType;
    private String inWarehouseCode;
    private LocalDate checkDate;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    private String provider;
    private LocalDateTime createTime;
}
