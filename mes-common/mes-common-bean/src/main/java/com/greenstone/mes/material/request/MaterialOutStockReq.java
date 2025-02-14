package com.greenstone.mes.material.request;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MaterialOutStockReq {

    @NotNull(message = "material.stock.lack.warehouse.id")
    private Long warehouseId;

    @Length(max = 200, message = "common.attribute.validation.remark")
    private String remark;

    @NotEmpty(message = "material.stock.lack.sponsor")
    private String sponsor;

    private Long targetWarehouseId;

    /**
     * 强制更新，忽略库存不足
     */
    private boolean ignoreStock;

    /**
     * 全部出库
     */
    private boolean allOut;


    @Valid
    private List<OutStockData> materialList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutStockData {

        private Long materialId;

        private String materialCode;

        private String materialVersion;

        @NotNull(message = "material.stock.lack.material.number")
        @Min(value = 1, message = "common.attribute.validation.amount.mix")
        @Max(value = 999999, message = "common.attribute.validation.amount.max")
        private Long number;

        private String orderCode;

        private String componentCode;

        /**
         * 领料单详情ID
         */
        private Long receivingDetailId;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
