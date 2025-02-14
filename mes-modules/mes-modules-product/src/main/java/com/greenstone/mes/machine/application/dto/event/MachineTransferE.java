package com.greenstone.mes.machine.application.dto.event;

import com.greenstone.mes.material.domain.BaseMaterial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineTransferE {
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    @NotEmpty(message = "请添加零件")
    @Valid
    private List<Part> parts;

    @Data
    public static class Part {
        private BaseMaterial material;
        @NotNull(message = "物料id不为空")
        private Long materialId;
        @NotEmpty(message = "零件号不为空")
        private String partCode;
        @NotEmpty(message = "零件名称不为空")
        private String partName;
        @NotEmpty(message = "零件版本不为空")
        private String partVersion;
        @NotNull(message = "数量不为空")
        private Long number;
        @NotEmpty(message = "出库仓库编码不为空")
        private String outWarehouseCode;
        @NotEmpty(message = "入库仓库编码不为空")
        private String inWarehouseCode;
        private String remark;
    }
}
