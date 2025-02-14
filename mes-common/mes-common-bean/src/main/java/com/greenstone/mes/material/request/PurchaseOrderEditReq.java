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
public class PurchaseOrderEditReq {

    @NotNull(message = "id不能为空")
    private Long id;

    private String remark;

    private String partOrderCode;

    @NotEmpty
    @Valid
    private List<PurchaseOrderAddReq.PurchaseOrderInfo> list;

}
