package com.greenstone.mes.material.application.dto;

import com.alibaba.fastjson2.JSON;
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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferStockCommand {

    /**
     * 出库仓库
     */
    private BaseWarehouse warehouseOut;

    /**
     * 入库仓库
     */
    private BaseWarehouse warehouseIn;

    /**
     * 零件组id
     */
    private Long partsGroupId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 经手人
     */
    private String sponsor;

    /**
     * 动作
     */
    private StockAction action;

    /**
     * 操作
     */
    private BillOperation operation;

    private boolean outboundAll;

    /**
     * 存放点动作
     */
    private StorePlaceAction storePlaceAction;

    private List<TransferMaterial> materialList;

    private NgData ngData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferMaterial {

        private BaseMaterial material;

        private Long number;

        private String worksheetCode;

        private String componentCode;

        private String projectCode;

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
