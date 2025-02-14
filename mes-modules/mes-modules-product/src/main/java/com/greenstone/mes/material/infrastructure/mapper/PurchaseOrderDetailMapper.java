package com.greenstone.mes.material.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购单详情Mapper接口
 *
 * @author wushaoqi
 * @date 2022-05-17-12:57
 */
@Repository
public interface PurchaseOrderDetailMapper extends BaseMapper<ProcessOrderDetailDO> {

    /**
     * 查询采购单详情
     *
     * @param id 采购单的ID
     */
    List<PurchaseOrderDetailResp> selectPurchaseOrderDetail(Long id);

    /**
     * 查询零件看板列表
     */
    List<PartBoardExportResp> selectPartsBoardList(PartsBoardListReq partsBoardListReq);

    List<WorksheetDetailResp> selectWorksheetDetail(WorksheetDetailListReq detailListReq);

    List<WorksheetPlaceOrder> selectWorksheetPlaceOrderList(WorkSheetPlaceOrderQuery placeOrderQuery);

    List<WorksheetCheck> selectWorksheetCheckList(WorkSheetCheckCountQuery checkCountQuery);
}
