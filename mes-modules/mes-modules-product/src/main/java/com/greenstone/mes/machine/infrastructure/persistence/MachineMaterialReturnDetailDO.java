package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2024-01-02-14:02
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_material_return_detail")
public class MachineMaterialReturnDetailDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 8728999612200511830L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String orderSerialNo;
    private String orderDetailId;
    private String projectCode;
    private Long materialId;
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long returnNumber;
}
