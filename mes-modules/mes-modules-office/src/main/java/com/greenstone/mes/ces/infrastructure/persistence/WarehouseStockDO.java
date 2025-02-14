package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * 仓库库存
 *
 * @author wushaoqi
 * @date 2023-06-05-13:15
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "ces_warehouse_stock")
public class WarehouseStockDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 8618633695395955532L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String warehouseCode;
    @TableField
    private String itemCode;
    @TableField
    private Long number;
}
