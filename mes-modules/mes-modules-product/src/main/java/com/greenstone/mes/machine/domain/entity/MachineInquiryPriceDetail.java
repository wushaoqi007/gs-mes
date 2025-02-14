package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineInquiryPriceDetail extends TableEntity {

    @StreamField("申请单号")
    private String requirementSerialNo;
    private Long requirementDetailId;
    @StreamField("项目代码")
    private String projectCode;
    @StreamField("层级结构")
    private String hierarchy;
    private Long materialId;
    @StreamField("零件号")
    private String partCode;
    @StreamField("零件名称")
    private String partName;
    @StreamField("零件版本")
    private String partVersion;
    @StreamField("零件数量")
    private Long partNumber;
    @StreamField("图纸数量")
    private Integer paperNumber;
    @StreamField("已扫描图纸数量")
    private Integer scannedPaperNumber;
    @StreamField("表面处理")
    private String surfaceTreatment;
    @StreamField("材料")
    private String rawMaterial;
    @StreamField("重量")
    private String weight;
    @StreamField("设计")
    private String designer;
    @StreamField("备注")
    private String remark;
    private Boolean ordered;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @StreamField("加工纳期")
    private LocalDate processDeadline;
}
