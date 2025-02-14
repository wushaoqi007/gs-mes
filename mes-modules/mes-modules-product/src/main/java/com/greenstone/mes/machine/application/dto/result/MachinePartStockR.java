package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-19-10:28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachinePartStockR {
    private String orderDetailId;
    private String orderSerialNo;
    private String requirementSerialNo;
    private String projectCode;
    private String provider;
    private Long stockNumber;
    private Long materialId;
    private String partCode;
    private String partVersion;
    private String partName;
    private Long warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private Integer stage;
    private String unit;
    private String rawMaterial;
    private String weight;
    private String surfaceTreatment;
    private String designer;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Long stayDays;
    private Long stayHours;
    private String duration;

}
