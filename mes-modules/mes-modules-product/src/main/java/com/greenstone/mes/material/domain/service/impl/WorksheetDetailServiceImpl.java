package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.material.application.dto.WorkSheetCheckCountQuery;
import com.greenstone.mes.material.application.dto.WorkSheetPlaceOrderQuery;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.domain.entity.WorksheetCheck;
import com.greenstone.mes.material.domain.entity.WorksheetPlaceOrder;
import com.greenstone.mes.material.domain.service.WorksheetDetailService;
import com.greenstone.mes.material.enums.WorkProcedureTypeToWareHouseCode;
import com.greenstone.mes.material.infrastructure.mapper.PurchaseOrderDetailMapper;
import com.greenstone.mes.material.request.PartsBoardListReq;
import com.greenstone.mes.material.request.WorksheetDetailListReq;
import com.greenstone.mes.material.response.PartBoardExportResp;
import com.greenstone.mes.material.response.PurchaseOrderDetailResp;
import com.greenstone.mes.material.response.WorksheetDetailResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PurchaseOrderServiceImpl接口实现
 *
 * @author wushaoqi
 * @date 2022-05-16-12:57
 */
@Service
public class WorksheetDetailServiceImpl extends ServiceImpl<PurchaseOrderDetailMapper, ProcessOrderDetailDO> implements WorksheetDetailService {
    @Autowired
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;


    @Override
    public List<PurchaseOrderDetailResp> selectPurchaseOrderDetail(Long id) {
        return purchaseOrderDetailMapper.selectPurchaseOrderDetail(id);
    }

    @Override
    public List<PartBoardExportResp> selectPartsBoardList(PartsBoardListReq partsBoardListReq) {
        // 工序不为空，转换工序为仓库dept_code
        if (StrUtil.isNotBlank(partsBoardListReq.getWorkProcedureName())) {
            partsBoardListReq.setWorkProcedureName(WorkProcedureTypeToWareHouseCode.getLabelByValue(partsBoardListReq.getWorkProcedureName()));
        }
        return purchaseOrderDetailMapper.selectPartsBoardList(partsBoardListReq);
    }

    @Override
    public List<WorksheetDetailResp> selectWorksheetDetail(WorksheetDetailListReq detailListReq) {
        return purchaseOrderDetailMapper.selectWorksheetDetail(detailListReq);
    }

    @Override
    public List<WorksheetPlaceOrder> selectWorksheetPlaceOrderList(WorkSheetPlaceOrderQuery placeOrderQuery) {
        return purchaseOrderDetailMapper.selectWorksheetPlaceOrderList(placeOrderQuery);
    }

    @Override
    public List<WorksheetCheck> selectWorksheetCheckList(WorkSheetCheckCountQuery checkCountQuery) {
        return purchaseOrderDetailMapper.selectWorksheetCheckList(checkCountQuery);
    }
}
