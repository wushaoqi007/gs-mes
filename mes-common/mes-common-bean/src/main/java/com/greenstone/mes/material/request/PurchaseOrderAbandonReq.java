package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PurchaseOrderAbandonReq {

    @NotNull(message = "采购单ID不为空")
    private Long purchaseOrderId;

    @Valid
    private List<Confirm> list;

    @Data
    public static class Confirm {

        @NotNull(message = "采购单详情ID不为空")
        private Long id;

        /**
         * 是否废弃
         */
        private String isAbandon;


        /**
         * 备注
         */
        private String remark;
    }


}
