package com.greenstone.mes.material.event.data;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.material.domain.BaseMaterial;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import com.greenstone.mes.material.infrastructure.enums.StockAction;
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
public class StockOperationEventData {

    private BaseWarehouse warehouse;

    private Long partsGroupId;

    private StockAction action;

    private BillOperation operation;

    private String sponsor;

    private String remark;

    private List<StockMaterial> materialList;

    private NgData ngData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMaterial {

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
