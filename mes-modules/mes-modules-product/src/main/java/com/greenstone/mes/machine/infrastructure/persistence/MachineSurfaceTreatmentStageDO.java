package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2023-12-14-11:27
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_surface_treatment_stage")
public class MachineSurfaceTreatmentStageDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7487631095945415107L;

    @TableId(type = IdType.AUTO)
    private String id;
    private String orderSerialNo;
    private String orderDetailId;
    private String checkSerialNo;
    private String checkDetailId;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private String surfaceTreatment;
    private String stageName;
    private Integer totalStage;
    private Integer currentStage;
}
