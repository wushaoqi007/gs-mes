package com.greenstone.mes.product.application.dto.cmd;

import com.greenstone.mes.product.infrastructure.enums.ProductPlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPlanStatusChangeCmd {

    @NotNull(message = "请输入正确的计划状态")
    private ProductPlanStatus planStatus;

    @NotEmpty(message = "请选择计划")
    private List<Long> ids;

}
