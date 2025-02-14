package com.greenstone.mes.bom.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomIntegrityResp {

    private String projectCode;

    private double projectIntegrityRate;

    private Long totalMaterialNumber;

    private Long lackMaterialNumber;

    private List<ComponentIntegrityInfo> componentList;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentIntegrityInfo {

        private String componentCode;

        private String componentName;

        private String componentVersion;

        private Long totalMaterialNumber;

        private Long lackMaterialNumber;

        private double componentIntegrityRate;

        private List<MaterialIntegrityInfo> materialList;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaterialIntegrityInfo {

        private String materialCode;

        private String materialName;

        private String materialVersion;

        private long totalNumber;

        private long existNumber;

        private long lackNumber;

        private boolean lack;

        private List<MaterialNumber> materialNumberList;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaterialNumber {

        private String whCode;

        private String whName;

        private Long number;

    }


}
