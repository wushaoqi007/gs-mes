package com.greenstone.mes.material.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockSearchListReq {

    /**
     * 物料ID
     */
    private Long materialId;

    /**
     * 指定仓库的阶段
     */
    private Integer stage;

}
