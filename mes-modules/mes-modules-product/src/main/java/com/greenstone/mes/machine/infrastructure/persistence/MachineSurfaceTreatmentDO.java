package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-14-11:25
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_surface_treatment")
public class MachineSurfaceTreatmentDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2109828533413914935L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private String surfaceTreatment;
    private LocalDateTime handleTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String provider;
    private String remark;
}
