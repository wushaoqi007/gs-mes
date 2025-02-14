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

/**
 * 收货单表
 *
 * @author wushaoqi
 * @date 2023-05-25-9:43
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_receipt")
public class ReceiptDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6730801373937872676L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private ProcessStatus status;
    @TableField
    private LocalDate receiveDate;
    @TableField
    private Long receiveBy;
    @TableField
    private String receiveByName;
    @TableField
    private String remark;
}
