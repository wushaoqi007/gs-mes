package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-11-23-15:19
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_requirement_detail")
public class MachineRequirementDetailDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String serialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long perSet;
    private Integer setsNumber;
    private Long processNumber;
    private Integer paperNumber;
    private Integer scannedPaperNumber;
    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    private LocalDateTime printDate;
    private String hierarchy;
    private String designer;
    private String remark;
    private String partType;
    @TableField("is_urgent")
    private Boolean urgent;

    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;
}
