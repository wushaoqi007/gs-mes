package com.greenstone.mes.material.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingDetailResp {


    /**
     * 领料单ID
     */
    private Long receivingId;

    /**
     * 领料单详情ID
     */
    private Long receivingDetailId;


    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 物料号
     */
    private String materialCode;

    /**
     * 物料版本
     */
    private String materialVersion;

    /**
     * 总数
     */
    private Integer totalNum;

    /**
     * 已领取数量
     */
    private Integer receivedNum;

    /**
     * 库存总数量
     */
    private Integer totalStockNum;

    /**
     * 仓库信息列表
     */
    private List<WarehouseInfo> warehouseList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class WarehouseInfo {
        /**
         * 仓库ID
         */
        private Long warehouseId;

        /**
         * 仓库code
         */
        private String warehouseCode;

        /**
         * 仓库名称
         */
        private String warehouseName;

        /**
         * 库存数量
         */
        private Integer stockNum;
    }

}
