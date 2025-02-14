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
public class PurchaseOrderChangeApplyReq {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotEmpty(message = "列表不为空或变量名称不正确")
    @Valid
    private List<PurchaseOrderInfo> applyList;

    /**
     * 机加工单详细信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PurchaseOrderInfo {

        /**
         * 详情ID
         */
        @NotNull(message = "机加工单详情ID不能为空")
        private Long detailId;

        /**
         * 申请变更数量
         */
        @NotNull(message = "申请变更数量不能为空")
        private Long applyNumber;

        private String remark;

        /**
         * 变更原因
         */
        @NotEmpty(message = "变更原因不能为空")
        private String applyReason;

    }

}
