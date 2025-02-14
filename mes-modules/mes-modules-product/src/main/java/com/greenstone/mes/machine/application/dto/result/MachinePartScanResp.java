package com.greenstone.mes.machine.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachinePartScanResp {

    private Long materialId;

    private String partCode;

    private String partVersion;

    private String partName;

    private String projectCode;

    private String requirementSerialNo;

    private List<Stock> stocks;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stock {

        private Long number;

        private Long orderNumber;

        private Long warehouseId;

        private String warehouseCode;

        private Long stockId;

        private Integer stage;

        private Integer operation;

        private String surfaceTreatmentSerialNo;

        private String reworkSerialNo;

        private String orderSerialNo;

    }

}
