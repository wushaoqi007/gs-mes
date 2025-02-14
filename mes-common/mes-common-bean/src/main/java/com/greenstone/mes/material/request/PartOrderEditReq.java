package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartOrderEditReq {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotEmpty(message = "机加工单编号不能为空")
    private String orderCode;

    @NotEmpty
    @Valid
    private List<PartOrderInfo> editList;

    /**
     * 机加工单详细信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartOrderInfo {

        @NotNull(message = "零件数量不能为空")
        private Long materialNumber;

        @NotNull(message = "图纸数量不能为空")
        private Integer paperNumber;

        @NotEmpty(message = "材料不能为空")
        private String rawMaterial;

        @NotEmpty(message = "表处不能为空")
        private String surfaceTreatment;

        @NotEmpty(message = "重量不能为空")
        private String weight;

        @NotEmpty(message = "设计不能为空")
        private String designer;

        /**
         * 购买理由
         */
        @NotNull(message = "购买原因不能为空")
        private Integer purchaseReason;

        @NotEmpty(message = "备注不能为空")
        private String remark;

        /**
         * 详情ID
         */
        @NotNull(message = "详情id不能为空")
        private Long detailId;

    }

}
