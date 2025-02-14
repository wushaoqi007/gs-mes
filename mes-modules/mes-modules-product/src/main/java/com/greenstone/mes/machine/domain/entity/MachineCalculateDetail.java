package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MachineCalculateDetail {

    private String id;
    private String serialNo;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long partNumber;
    // 价格信息
    private Double totalPrice;
    private Double calculatePrice;
    private String calculateJson;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calculateTime;
    private String calculateBy;
    private Long calculateById;

    // 物料表信息
    private Double materialCalculatePrice;
    private String materialCalculateJson;
    private LocalDateTime materialCalculateTime;
    private String materialCalculateBy;
    private Long materialCalculateById;
    private String rawMaterial;
    private String weight;
}
