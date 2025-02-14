package com.greenstone.mes.machine.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-01-16:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MachineCalculateResult {
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String calculateBy;
    private Long calculateById;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;
    private String confirmBy;
    private List<Part> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
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

        private String rawMaterial;
        private String weight;
    }
}
