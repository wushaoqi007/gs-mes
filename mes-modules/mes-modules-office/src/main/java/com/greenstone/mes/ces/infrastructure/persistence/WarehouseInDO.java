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
 * 入库单
 *
 * @author wushaoqi
 * @date 2023-06-2-9:43
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_warehouse_in")
public class WarehouseInDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4215496076004213273L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @TableField
    private String serialNo;
    @TableField
    private String warehouseCode;
    @TableField
    private ProcessStatus status;
    @TableField
    private LocalDate inDate;
    @TableField
    private Long sponsorId;
    @TableField
    private String sponsorName;
    @TableField
    private LocalDateTime handleDate;
    @TableField
    private String remark;
}
