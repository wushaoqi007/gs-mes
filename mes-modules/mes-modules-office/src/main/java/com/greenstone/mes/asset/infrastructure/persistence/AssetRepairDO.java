package com.greenstone.mes.asset.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.asset.infrastructure.enums.AssetRepairStatus;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "asset_repair")
public class AssetRepairDO extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String serialNo;
    private LocalDate registrationDate;
    private Long handleById;
    private String handleBy;
    private Long repairById;
    private String repairBy;
    private AssetRepairStatus status;
    private String repairExpense;
    private String repairContent;
}
