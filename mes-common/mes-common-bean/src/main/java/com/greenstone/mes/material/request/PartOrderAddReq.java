package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartOrderAddReq {

    private Long orderId;

    private String orderCode;

    @NotEmpty
    private String projectCode;

    @Valid
    @NotEmpty(message = "机加工单中未包含零件信息")
    private List<OrderDetail> orderDetailList;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class OrderDetail {

        @NotEmpty(message = "组件编码不能为空")
        private String componentCode;

        @NotEmpty(message = "组件名称不能为空")
        private String componentName;

        @NotEmpty(message = "零件编码不能为空")
        private String partCode;

        @NotEmpty(message = "零件版本不能为空")
        private String partVersion;

        @NotEmpty(message = "零件名称不能为空")
        private String partName;

        @NotNull(message = "零件数量不能为空")
        private Long partNumber;

        @NotNull(message = "图纸数量不能为空")
        private Integer paperNumber;

        private String rawMaterial;

        private String surfaceTreatment;

        private String weight;

        @NotEmpty(message = "设计不能为空")
        private String designer;

        @Max(value = 6, message = "购买原因填写1-6内数字，1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他")
        @Min(value = 1, message = "购买原因填写1-6内数字，1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他")
        @NotNull(message = "购买原因不能为空")
        private Integer purchaseReason;

        @NotNull(message = "公司类型不能为空")
        private Integer companyType;

    }

}
