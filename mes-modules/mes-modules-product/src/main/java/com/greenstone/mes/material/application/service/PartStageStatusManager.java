package com.greenstone.mes.material.application.service;

import com.alibaba.excel.ExcelWriter;
import com.greenstone.mes.material.application.dto.PartProgressQuery;
import com.greenstone.mes.material.application.dto.result.PartProgressR;
import com.greenstone.mes.material.application.dto.result.ProjectProgressR;
import com.greenstone.mes.material.domain.ProcessOrderDO;
import com.greenstone.mes.material.domain.ProcessOrderDetailDO;
import com.greenstone.mes.material.event.data.StockOperationEventData;
import com.greenstone.mes.material.event.data.StockUpdateEventData;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.request.PartsReworkStatReq;
import com.greenstone.mes.material.response.MaterialWorksheetProgressListResp;
import com.greenstone.mes.material.response.MaterialWorksheetProgressStatResp;
import com.greenstone.mes.material.response.PartReworkStatResp;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 零件阶段状态接口
 *
 * @author wushaoqi
 * @date 2022-12-13-15:10
 */
public interface PartStageStatusManager {

    void savePartStageStatus(StockOperationEventData source);

    /**
     * 记录待收件状态
     *
     * @param processOrder             加工单
     * @param processOrderDetailDOList 加工单明细列表
     */
    void goToBeReceived(ProcessOrderDO processOrder, List<ProcessOrderDetailDO> processOrderDetailDOList);

    /**
     * 返工率统计
     */
    List<PartReworkStatResp> reworkStat(PartsReworkStatReq partsReworkStatReq);

    /**
     * 进度统计查询
     *
     * @param progressStatReq
     */
    MaterialWorksheetProgressStatResp progressStatistics(MaterialWorksheetProgressStatReq progressStatReq);

    /**
     * 零件进度列表
     *
     * @param progressStatReq
     */
    List<MaterialWorksheetProgressListResp> selectProgressList(MaterialWorksheetProgressStatReq progressStatReq);

    List<MaterialWorksheetProgressListResp> selectUnfinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq);

    List<MaterialWorksheetProgressListResp> selectFinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq);

    List<MaterialWorksheetProgressListResp> selectUsedProgressList(MaterialWorksheetProgressStatReq progressStatReq);

    ProjectProgressR selectProjectProgress(PartProgressQuery partProgressQuery);

    List<PartProgressR> selectPartProgress(PartProgressQuery partProgressQuery);

    ExcelWriter makeProjectPartProgressExcel(HttpServletResponse response,PartProgressQuery partProgressQuery);

    void updatePartStageStatus(StockUpdateEventData stockUpdateEventData);
}
