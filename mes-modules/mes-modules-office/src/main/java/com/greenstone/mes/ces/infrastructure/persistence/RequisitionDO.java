package com.greenstone.mes.ces.infrastructure.persistence;

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
 * @date 2023-11-08-10:39
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_requisition")
public class RequisitionDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8906111382211785096L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private ProcessStatus status;
    private LocalDateTime requisitionDate;
    private Long requisitionerId;
    private String requisitionerName;
    private String requisitionerNo;
    private String remark;
}
