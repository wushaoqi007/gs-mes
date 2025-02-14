package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.infrastructure.enums.UseStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-01-03-14:02
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_material_use")
public class MachineMaterialUseDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -7950337132303699019L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private String materialRequirementSerialNo;
    private ProcessStatus status;
    private UseStatus useStatus;
    private LocalDateTime useTime;
    private String sponsor;
    private Long sponsorId;
    private String sponsorNo;
    private String operator;
    private Long operatorId;
    private String operatorNo;
    private String remark;
}
