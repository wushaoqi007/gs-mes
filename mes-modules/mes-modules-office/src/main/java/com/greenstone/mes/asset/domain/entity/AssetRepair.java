package com.greenstone.mes.asset.domain.entity;

import com.greenstone.mes.asset.infrastructure.enums.AssetRepairStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetRepair {

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
    private List<Asset> assets;
}
