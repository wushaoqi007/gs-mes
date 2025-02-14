package com.greenstone.mes.material.application.service;

import com.greenstone.mes.material.application.dto.WorkSheetCheckCountQuery;
import com.greenstone.mes.material.application.dto.WorkSheetPlaceOrderQuery;
import com.greenstone.mes.material.application.dto.PartCheckCmd;
import com.greenstone.mes.material.application.dto.result.WorksheetCheckCountR;
import com.greenstone.mes.material.application.dto.result.WorksheetPlaceOrderR;
import com.greenstone.mes.material.cqe.command.WorksheetImportCommand;
import com.greenstone.mes.material.cqe.command.WorksheetImportEditCommand;
import com.greenstone.mes.material.cqe.command.WorksheetSaveCommand;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.dto.PartInGoodStockDto;
import com.greenstone.mes.material.dto.PartReceiveDto;
import com.greenstone.mes.material.request.*;
import com.greenstone.mes.material.response.PartBoardExportResp;
import com.greenstone.mes.material.response.PurchaseOrderExportResp;

import javax.validation.Valid;
import java.util.List;

public interface WorksheetManager {

    /**
     * 新增采购单
     */
    void purchaseOrderAdd(PurchaseOrderAddReq purchaseOrderAddReq);

    /**
     * 新增采购
     *
     * @param partOrderAddReq partOrderAddReq
     * @return 采购单ID
     */
    Long addPartOrder(PartOrderAddReq partOrderAddReq);

    /**
     * 确认采购单详情
     */
    void confirmPurchaseOrder(PurchaseOrderConfirmReq purchaseOrderConfirmReq);

    /**
     * 更新采购单详情
     */
    void updatePurchaseOrderDetail(PurchaseOrderConfirmReq purchaseOrderConfirmReq);

    /**
     * 废弃零件
     *
     * @param purchaseOrderAbandonReq purchaseOrderAbandonReq
     */
    void abandonPurchaseOrderDetail(PurchaseOrderAbandonReq purchaseOrderAbandonReq);

    /**
     * 废弃采购单
     */
    void giveUpPurchaseOrder(PurchaseOrderEditReq purchaseOrderEditReq);

    /**
     * 导出采购单
     */
    List<PurchaseOrderExportResp> exportPurchaseOrder(Long id);

    /**
     * 查询零件看板
     */
    List<PartBoardExportResp> listPartBoardExportData(PartsBoardListReq partsBoardListReq);

    /**
     * 零件接收更新机加工单
     *
     * @param partReceiveDto 零件接收数据
     */
    void partReceiveAction(PartReceiveDto partReceiveDto);

    /**
     * 零件入良品库更新机加工单
     */
    void partInGoodStockAction(PartInGoodStockDto partInGoodStockDto);

    /**
     * 导入机加工单
     *
     * @param importCommand 导入命令
     */
    void importWorksheet(@Valid WorksheetImportCommand importCommand);

    void saveWorksheet(@Valid WorksheetSaveCommand saveCommand);

    /**
     * 机加工单修改
     *
     * @param partOrderEditReq 修改后的机加工单
     */
    void editPurchaseOrder(PartOrderEditReq partOrderEditReq);

    /**
     * 变更申请
     *
     * @param purchaseOrderChangeApplyReq 机加工单变更信息
     */
    void changeApplyPurchaseOrder(PurchaseOrderChangeApplyReq purchaseOrderChangeApplyReq);

    /**
     * 变更确认
     *
     * @param purchaseOrderChangeConfirmReq 机加工单确认变更信息
     */
    void changeConfirmPurchaseOrder(PurchaseOrderChangeConfirmReq purchaseOrderChangeConfirmReq);

    /**
     * 查询变更详情
     *
     * @param orderId 机加工单ID
     * @return
     */
    List<ProcessOrderDetailDO> getChangeDetail(Long orderId);

    /**
     * 更新机加工单零件信息
     */
    void updatePartOrderInfo(List<PartOrderInfoEdit> partOrderInfoEditList);

    /**
     * 加工单导入修改
     * @param importEditCommand 导入命令
     */
    void importEditWorksheet(@Valid WorksheetImportEditCommand importEditCommand);

    /**
     * 加工单删除
     * @param id
     */
    void removeWorksheetById(Long id);

    List<WorksheetPlaceOrderR> selectWorksheetPlaceOrderList(WorkSheetPlaceOrderQuery placeOrderQuery);

    List<WorksheetCheckCountR> selectWorksheetCheckCountList(WorkSheetCheckCountQuery checkCountQuery);

    ProcessOrderDetailDO checkPart(PartCheckCmd partCheckCmd);
}
