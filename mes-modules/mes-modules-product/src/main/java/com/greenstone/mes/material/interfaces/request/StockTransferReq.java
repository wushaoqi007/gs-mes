package com.greenstone.mes.material.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author gu_renkai
 * @date 2022/12/14 9:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferReq {
    private Integer operation;
    private Long inStockWhId;
    private Long outStockWhId;
    private Long partsGroupId;
    private String sponsor;
    private String remark;
    private boolean operateAll;
    private List<Material> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Material {
        private Long materialId;
        private Long number;
        private String componentCode;
        private String projectCode;
        private String worksheetCode;
    }

}
