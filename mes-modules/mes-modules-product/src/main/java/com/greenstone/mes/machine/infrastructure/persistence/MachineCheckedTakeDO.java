package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_checked_take")
public class MachineCheckedTakeDO extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime takeTime;
    private String takeBy;
    private Long takeById;
    private String takeByNo;
    private String sponsor;
    private Long sponsorId;
    private String remark;
    @TableField("is_signed")
    private Boolean signed;
    private String spNo;
    @TableField("is_imported")
    private Boolean imported;
}
