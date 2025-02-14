package com.greenstone.mes.machine.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MachineRequirementDetail extends TableEntity {
    @StreamField("项目代码")
    private String projectCode;
    private Long materialId;
    @StreamField("零件号")
    private String partCode;
    @StreamField("零件名称")
    private String partName;
    @StreamField("零件版本")
    private String partVersion;
    @StreamField("单套数量")
    private Long perSet;
    @StreamField("套数")
    private Integer setsNumber;
    @StreamField("总数")
    private Long processNumber;
    @StreamField("图纸张数")
    private Integer paperNumber;
    private Integer scannedPaperNumber;
    @StreamField("表面处理")
    private String surfaceTreatment;
    @StreamField("材料")
    private String rawMaterial;
    @StreamField("质量")
    private String weight;
    @StreamField("打印日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime printDate;
    @StreamField("层级结构")
    private String hierarchy;
    @StreamField("设计")
    private String designer;
    private String partType;
    private Boolean urgent;
    @StreamField("备注")
    private String remark;
}
