package com.greenstone.mes.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 良品入库
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartInGoodStockDto {

    private List<PartInGoodStockDetail> partInGoodStockDetailList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PartInGoodStockDetail {

        /**
         * 机加工单号
         */
        private String partOrderCode;

        /**
         * 组件编号
         */
        private String componentCode;

        /**
         * 零件编号
         */
        private String partCode;

        /**
         * 零件版本
         */
        private String partVersion;

        /**
         * 良品数量
         */
        private Long goodNumber;

    }

}
