package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-14-11:27
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_surface_treatment_detail")
public class MachineSurfaceTreatmentDetailDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5225404200250971257L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String checkSerialNo;
    private String checkDetailId;
    private String requirementSerialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long processNumber;
    private Long handleNumber;
    private LocalDateTime receiveTime;
    private Long receivedNumber;
    private String warehouseCode;
    private String provider;
    private String surfaceTreatment;
}
