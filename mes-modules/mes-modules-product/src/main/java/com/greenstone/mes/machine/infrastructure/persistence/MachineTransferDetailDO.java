package com.greenstone.mes.machine.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "machine_transfer_detail")
public class MachineTransferDetailDO extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private String id;
    private String serialNo;
    private Long materialId;
    private String outWarehouseCode;
    private String inWarehouseCode;
    private String partCode;
    private String partName;
    private String partVersion;
    private Long number;
    private String remark;
}
