package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_inquiry_price_detail")
public class MachineInquiryPriceDetailDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String serialNo;
    private String requirementSerialNo;
    private String requirementDetailId;
    private String projectCode;
    private String hierarchy;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long partNumber;
    private Integer paperNumber;
    private Integer scannedPaperNumber;

    private String surfaceTreatment;
    private String rawMaterial;
    private String weight;
    private String designer;
    private String remark;

    @TableField("is_ordered")
    private Boolean ordered;

    private LocalDate processDeadline;

    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

}
