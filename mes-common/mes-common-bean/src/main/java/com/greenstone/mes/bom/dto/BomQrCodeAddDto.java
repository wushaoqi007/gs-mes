package com.greenstone.mes.bom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 通过二维码添加BOM的请求类
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BomQrCodeAddDto {

    @NotEmpty(message = "缺少机加工单号")
    private String partOrderCode;

    @NotEmpty(message = "缺少项目代码")
    private String projectCode;

    @NotEmpty(message = "缺少组件号")
    private String componentCode;

    @NotEmpty(message = "缺少组件名称")
    private String componentName;

    @NotEmpty(message = "缺少零件号")
    private String partCode;

    @NotEmpty(message = "缺少零件名称")
    private String partName;

    @NotEmpty(message = "缺少零件版本")
    private String partVersion;

    @NotEmpty(message = "缺少零件数量")
    private Long partNumber;

    @NotEmpty(message = "缺少图纸张数")
    private Integer paperNumber;

    @NotEmpty(message = "缺少重量")
    private String weight;

    @NotEmpty(message = "缺少原材料")
    private String rawMaterial;

    @NotEmpty(message = "缺少表面处理")
    private String surfaceTreatment;

    @NotEmpty(message = "缺少设计姓名")
    private String designer;

    @NotNull(message = "购买原因不能为空")
    private Integer purchaseReason;

    @NotNull(message = "公司类型不能为空")
    private Integer companyType;

}
