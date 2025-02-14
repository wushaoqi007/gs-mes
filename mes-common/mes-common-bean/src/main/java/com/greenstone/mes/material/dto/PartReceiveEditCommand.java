package com.greenstone.mes.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartReceiveEditCommand {

    private Long recordId;

    private List<PartInfo> partInfoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PartInfo {
        private Long userId;

        private String receiveTime;

        private Long materialId;

        private String worksheetCode;

        private String projectCode;

        private String componentCode;

        private String partCode;

        private String partVersion;

        private String partName;

        private Long number;

        private Boolean handle;

        private Long warehouseId;
    }
}
