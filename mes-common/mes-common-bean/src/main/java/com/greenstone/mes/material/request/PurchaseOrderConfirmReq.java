package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderConfirmReq {

    @NotNull(message = "采购单ID不为空")
    private Long purchaseOrderId;

    @Valid
    private List<Confirm> list;

    /**
     * 采购单比对确认
     */
    @Data
    public static class Confirm {

        @NotNull(message = "采购单详情ID不为空")
        private Long id;

        /**
         * 数量
         */
        @NotNull(message = "数量不为空")
        private Long number;

        /**
         * 已扫描数量
         */
        @NotNull(message = "已扫描数量不为空")
        private Integer scanNumber;

        /**
         * 是否采购
         */
        private String isPurchase;

        /**
         * 是否加急
         */
        private String isFast;
        private Boolean isUpdateParts;
        private Boolean isRepairParts;

        /**
         * 是否废弃
         */
        private String isAbandon;

        /**
         * 加工单位
         */
        private String provider;

        /**
         * 加工纳期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date processingTime;

        /**
         * 计划纳期
         */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date planTime;

        /**
         * 类型:加工件、标准件
         */
        private String type;

        /**
         * 比对结果
         */
        private String comparisonResult;

        /**
         * 备注
         */
        private String remark;
    }


}
