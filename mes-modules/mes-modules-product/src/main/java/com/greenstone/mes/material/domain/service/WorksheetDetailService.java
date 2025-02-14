package com.greenstone.mes.material.domain.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.material.application.dto.WorkSheetCheckCountQuery;
import com.greenstone.mes.material.application.dto.WorkSheetPlaceOrderQuery;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.entity.WorksheetCheck;
import com.greenstone.mes.material.domain.entity.WorksheetPlaceOrder;
import com.greenstone.mes.material.request.PartsBoardListReq;
import com.greenstone.mes.material.request.WorksheetDetailListReq;
import com.greenstone.mes.material.response.PartBoardExportResp;
import com.greenstone.mes.material.response.PurchaseOrderDetailResp;
import com.greenstone.mes.material.response.WorksheetDetailResp;

import java.util.List;

/**
 * 采购单详情Service接口
 *
 * @author wushaoqi
 * @date 2022-05-11-12:57
 */
public interface WorksheetDetailService extends IServiceWrapper<ProcessOrderDetailDO> {

    /**
     * 查询采购单列表
     *
     * @param id 采购单的ID
     */
    List<PurchaseOrderDetailResp> selectPurchaseOrderDetail(Long id);

    /**
     * 查询零件看板
     */
    List<PartBoardExportResp> selectPartsBoardList(PartsBoardListReq partsBoardListReq);

    List<WorksheetDetailResp> selectWorksheetDetail(WorksheetDetailListReq detailListReq);

    List<WorksheetPlaceOrder> selectWorksheetPlaceOrderList(WorkSheetPlaceOrderQuery placeOrderQuery);

    List<WorksheetCheck> selectWorksheetCheckList(WorkSheetCheckCountQuery checkCountQuery);
}
