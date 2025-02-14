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
 * 出库单
 *
 * @author wushaoqi
 * @date 2023-06-5-9:43
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_warehouse_out")
public class WarehouseOutDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -307016243702567231L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String warehouseCode;
    @TableField
    private ProcessStatus status;
    @TableField
    private LocalDate outDate;
    @TableField
    private Long recipientId;
    @TableField
    private String recipientName;
    @TableField
    private Long sponsorId;
    @TableField
    private String sponsorName;
    @TableField
    private LocalDateTime handleDate;
    @TableField
    private String remark;
}
