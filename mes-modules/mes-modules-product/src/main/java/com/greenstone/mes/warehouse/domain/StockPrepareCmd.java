package com.greenstone.mes.warehouse.domain;

import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockPrepareCmd {

    /**
     * 单据操作，业务操作，对应各种单据
     */
    private BillOperation operation;

    /**
     * 单据编号
     */
    private String serialNo;

    /**
     * 经手人
     */
    private String sponsor;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请人工号
     */
    private String applicantNo;

    /**
     * 供应商，收货单属性
     */
    private String provider;

    /**
     * 备注
     */
    private String remark;

    private List<StockPrepareMaterial> materialList;

}
