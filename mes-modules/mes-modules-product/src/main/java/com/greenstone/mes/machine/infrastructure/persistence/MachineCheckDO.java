package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.machine.infrastructure.enums.CheckResultType;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2023-12-11-11:25
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_check")
public class MachineCheckDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 3640682231352732277L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private CheckResultType checkResultType;
    private LocalDateTime checkTime;
    private String checkBy;
    private Long checkById;
    private String checkByNo;
    @TableField("is_finished")
    private Boolean finished;
    private String remark;
}
