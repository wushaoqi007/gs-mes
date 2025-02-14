package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderAddReq {

    @NotEmpty
    @Valid
    private List<PurchaseOrderInfo> list;

    /**
     * 机加工单详细信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PurchaseOrderInfo {

        @NotEmpty(message = "项目代码不能为空")
        private String projectCode;

        private String partOrderCode;

        private String componentCode;

        @NotEmpty(message = "组件名称不能为空")
        private String componentName;

        @NotEmpty(message = "零件编码不能为空")
        private String code;

        @NotEmpty(message = "零件版本不能为空")
        private String version;

        @NotEmpty(message = "零件名称不能为空")
        private String name;

        private String buyLimit;

        @NotNull(message = "零件数量不能为空")
        private Long materialNumber;

        @NotNull(message = "图纸数量不能为空")
        private Integer paperNumber;

        @NotNull(message = "打印日期不能为空")
        private Date printData;

        private String rawMaterial;

        private String surfaceTreatment;

        private String weight;

        @NotEmpty(message = "设计不能为空")
        private String designer;

        /**
         * 购买理由
         */
        private Integer purchaseReason;

        private String remark;

        /**
         * 详情ID
         */
        private Long detailId;

        private String status;

        /**
         * 变更数量
         */
        private Long changeNumber;

        /**
         * 实际变更数量
         */
        private Long actualNumber;

        /**
         * 公司类型（1:无锡格林司通自动化设备有限公司、2:无锡格林司通科技有限公司）
         */
        private Integer companyType;

    }

}
