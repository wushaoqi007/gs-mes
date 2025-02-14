package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.material.infrastructure.enums.WarehouseStage;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_transfer")
public class MachineTransferDO extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private WarehouseStage stage;
    private LocalDateTime transferTime;
    private String transferBy;
    private Long transferById;
    private String transferByNo;
    private String remark;
}
