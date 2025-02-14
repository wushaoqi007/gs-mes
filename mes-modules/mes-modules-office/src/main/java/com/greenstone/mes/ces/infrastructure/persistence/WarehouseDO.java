package com.greenstone.mes.ces.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.ces.enums.WarehouseStatus;
import lombok.*;

import java.io.Serial;

/**
 * 仓库
 *
 * @author wushaoqi
 * @date 2023-06-01-10:02
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("ces_warehouse")
public class WarehouseDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3633600317776940750L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField
    private String parentWarehouseCode;

    @TableField
    private String idHierarchy;

    @TableField
    private String nameHierarchy;

    @TableField
    private String warehouseCode;

    @TableField
    private String warehouseName;

    @TableField
    private WarehouseStatus status;


}
