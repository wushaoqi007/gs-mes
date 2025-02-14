
package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialReceivingOutStockReq {

    /**
     * 领料单ID
     */
    @NotNull(message = "领料单ID不为空")
    private Long receivingId;

    /**
     * 零件ID(领料单与bom详情（零件）关联的id)
     */
    @NotNull(message = "零件ID不为空")
    private Long partId;

    /**
     * 仓库ID
     */
    @NotNull(message = "仓库ID不为空")
    private Long warehouseId;

    /**
     * 库存数量
     */
    @NotNull(message = "出库数量不为空")
    @Min(value = 1, message = "最小出库数量为1")
    @Max(value = 999999, message = "最大出库数量为999999")
    private Integer number;

    @NotEmpty(message = "经手人不为空")
    private String sponsor;

}
