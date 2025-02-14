package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

/**
 * @author wushaoqi
 * @date 2023-12-18-11:27
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_stock_change_detail")
public class MachineStockChangeDetailDO extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -6901655179301563623L;
    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private String projectCode;
    private Long materialId;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long stockNumber;
    private Long changeNumber;
    private String warehouseCode;
    private String remark;
}
