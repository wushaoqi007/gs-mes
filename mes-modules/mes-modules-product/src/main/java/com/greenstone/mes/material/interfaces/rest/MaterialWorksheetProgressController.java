package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.security.annotation.RequiresPermissions;
import com.greenstone.mes.material.application.service.PartStageStatusManager;
import com.greenstone.mes.material.request.MaterialWorksheetProgressStatReq;
import com.greenstone.mes.material.response.MaterialWorksheetProgressListResp;
import com.greenstone.mes.material.response.MaterialWorksheetProgressStatResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 加工单进度
 *
 * @author wushaoqi
 * @date 2022-11-09-13:09
 */
@Slf4j
@RestController
@RequestMapping("/worksheet/progress")
public class MaterialWorksheetProgressController extends BaseController {

    @Autowired
    private PartStageStatusManager partStageStatusManager;

    /**
     * 进度统计查询
     */
    @GetMapping("/stat")
    public AjaxResult selectProgressStatistics(MaterialWorksheetProgressStatReq progressStatReq) {
        MaterialWorksheetProgressStatResp list = partStageStatusManager.progressStatistics(progressStatReq);
        return AjaxResult.success(list);
    }

    /**
     * 零件进度列表
     */
    @GetMapping("/list")
    public AjaxResult selectProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        List<MaterialWorksheetProgressListResp> list = partStageStatusManager.selectProgressList(progressStatReq);
        return AjaxResult.success(list);
    }

    /**
     * 零件进度列表(未完成)
     */
    @GetMapping("/list/unfinished")
    public AjaxResult selectUnfinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        List<MaterialWorksheetProgressListResp> list = partStageStatusManager.selectUnfinishedProgressList(progressStatReq);
        return AjaxResult.success(list);
    }

    /**
     * 零件进度列表(已完成)
     */
    @GetMapping("/list/finished")
    public AjaxResult selectFinishedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        List<MaterialWorksheetProgressListResp> list = partStageStatusManager.selectFinishedProgressList(progressStatReq);
        return AjaxResult.success(list);
    }

    /**
     * 零件进度列表(已领用)
     */
    @GetMapping("/list/used")
    public AjaxResult selectUsedProgressList(MaterialWorksheetProgressStatReq progressStatReq) {
        List<MaterialWorksheetProgressListResp> list = partStageStatusManager.selectUsedProgressList(progressStatReq);
        return AjaxResult.success(list);
    }
}
