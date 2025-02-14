package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.request.PurchaseOrderListReq;
import com.greenstone.mes.material.response.PurchaseOrderListResp;

import java.util.List;

/**
 * 采购单Service接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface WorksheetService extends IServiceWrapper<ProcessOrderDO> {

    /**
     * 查询采购单列表
     *
     * @param purchaseOrderListReq
     */
    List<PurchaseOrderListResp> selectPurchaseOrderList(PurchaseOrderListReq purchaseOrderListReq);

    ProcessOrderDO selectByCode(String code);
}
