package com.greenstone.mes.material.application.dto;

import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
import com.greenstone.mes.material.infrastructure.enums.StorePlaceAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/14 9:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockOperationCommand {
    @NotNull(message = "不支持此操作")
    private BillOperation operation;
    private StockAction action;
    private BaseWarehouse warehouse;
    private BaseWarehouse toWarehouse;
    private Long partsGroupId;
    @NotEmpty(message = "经手人不能为空")
    private String sponsor;
    private String remark;
    private boolean outboundAll;
    private StorePlaceAction storePlaceAction = StorePlaceAction.NONE;

    private NgData ngData;

    @Valid
    private List<TransferMaterial> materialList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransferMaterial {
        @NotNull(message = "物料不能为空")
        private BaseMaterial material;
        @NotNull(message = "物料数量不能为空")
        private Long number;
        @NotEmpty(message = "组件号不能为空")
        private String componentCode;
        @NotEmpty(message = "项目代码不能为空")
        private String projectCode;
        @NotEmpty(message = "加工单号不能为空")
        private String worksheetCode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NgData {
        private String ngType;
        private String subNgType;
        private List<MultipartFile> files;
    }
}
