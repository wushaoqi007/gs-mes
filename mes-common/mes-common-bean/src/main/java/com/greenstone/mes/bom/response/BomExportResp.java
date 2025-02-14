package com.greenstone.mes.bom.response;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BomExportResp {

    @Excel(name = "项目代码")
    private String projectCode;

    @Excel(name = "组件号")
    private String componentCode;

    @Excel(name = "组件名称")
    private String componentName;

    @Excel(name = "组件版本")
    private String componentVersion;

    @Excel(name = "零件号")
    private String materialCode;

    @Excel(name = "零件名称")
    private String materialName;

    @Excel(name = "零件版本")
    private String materialVersion;

    @Excel(name = "材料")
    private String rawMaterial;

    @Excel(name = "表面处理")
    private String surfaceTreatment;

    @Excel(name = "订单数量")
    private Long number;

    @Excel(name = "设计")
    private String designer;

}
