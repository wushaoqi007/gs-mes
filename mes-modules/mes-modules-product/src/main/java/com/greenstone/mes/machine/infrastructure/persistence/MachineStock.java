package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 物料库存对象 material_stock
 *
 * @author gu_renkai
 * @date 2022-01-21
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("machine_stock")
public class MachineStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 仓库ID
     */
    @TableField
    private Long warehouseId;

    /**
     * 项目号
     */
    @TableField
    private String projectCode;

    /**
     * 物料ID
     */
    @TableField
    private Long materialId;

    /**
     * 库存数量
     */
    @TableField
    private Long number;

}
