package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCheckRecord {
    private String checkDetailId;
    private String checkSerialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String designer;
    private Long processNumber;
    private Long takeNumber;
    private Long checkedNumber;
    private Long toBeCheckedNumber;
    private Long stockNumber;
    private String outWarehouseCode;
    private CheckResult checkResult;
    private CheckResultType checkResultType;
    private String inWarehouseCode;
    private String inWarehouseName;
    private Long inWarehouseId;
    private String ngType;
    private String subNgType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String checkBy;
    private String provider;
}
