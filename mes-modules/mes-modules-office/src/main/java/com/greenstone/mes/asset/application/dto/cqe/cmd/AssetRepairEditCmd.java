package com.greenstone.mes.asset.application.dto.cqe.cmd;

import com.greenstone.mes.asset.infrastructure.enums.AssetRepairStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class AssetRepairEditCmd {

    @NotNull(message = "id不为空")
    private String id;
    @NotEmpty(message = "单号不为空")
    private String serialNo;
    @NotNull(message = "登记日期不为空")
    private LocalDate registrationDate;
    @NotNull(message = "报修人id不为空")
    private Long repairById;
    @NotEmpty(message = "报修人不为空")
    private String repairBy;
    private AssetRepairStatus status;
    private String repairExpense;
    @NotEmpty(message = "维修内容不为空")
    private String repairContent;

    @NotEmpty(message = "请添加维修物品")
    @Valid
    private List<Asset> assets;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Asset {
        @NotEmpty(message = "资产编码不为空")
        private String barCode;

    }

}
