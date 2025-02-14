package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.external.enums.ProcessStatus;
import lombok.*;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/21 15:11
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_application")
public class CesApplicationDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4875391401174685736L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private ProcessStatus status;
    @TableField
    private LocalDate expectReceiveDate;
    @TableField
    private String remark;
    @TableField
    private Long appliedBy;
    @TableField
    private String appliedByName;
    @TableField
    private LocalDateTime appliedTime;


}
