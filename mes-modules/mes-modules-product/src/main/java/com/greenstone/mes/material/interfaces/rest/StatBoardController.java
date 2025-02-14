package com.greenstone.mes.material.interfaces.rest;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.StatWeekQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;
import com.greenstone.mes.material.domain.service.StatPartsProgressService;
import com.greenstone.mes.material.domain.service.StatResultWeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计类接口
 *
 * @author wushaoqi
 * @date 2023-02-27-15:00
 */
@Slf4j
@RestController
@RequestMapping("/stat")
public class StatBoardController extends BaseController {
    private final StatResultWeekService statResultWeekService;
    private final StatPartsProgressService statPartsProgressService;

    public StatBoardController(StatResultWeekService statResultWeekService, StatPartsProgressService statPartsProgressService) {
        this.statResultWeekService = statResultWeekService;
        this.statPartsProgressService = statPartsProgressService;
    }


    /**
     * 周统计
     */
    @PostMapping("/week")
    public AjaxResult weekStat() {
        log.info("stat week start");
        statResultWeekService.weekStatistics();
        return AjaxResult.success("周统计,计算中...");
    }

    /**
     * 周不良率
     */
    @GetMapping("/week/rework/rate")
    public AjaxResult selectWeekReworkRate(StatWeekQuery query) {
        List<StatWeekReworkR> result = statResultWeekService.selectWeekReworkRate(query);
        return AjaxResult.success(result);
    }

    /**
     * 零件进度统计
     */
    @PostMapping("/parts/progress")
    public AjaxResult partsProgressStat(@RequestBody(required = false) StatProgressCmd statProgressCmd) {
        log.info("stat parts progress start");
        statPartsProgressService.partsProgressStatistics(statProgressCmd);
        return AjaxResult.success("零件进度统计,计算中...");
    }

    /**
     * 在制项目数据数据分析
     */
    @GetMapping("/project/analyse")
    public AjaxResult projectAnalyse(StatQuery query) {
        List<StatProjectAnalyseR> result = statPartsProgressService.projectAnalyse(query);
        return AjaxResult.success(result);
    }

    /**
     * 在制项目数据数据分析（图）
     */
    @GetMapping("/project/analyse/chart")
    public AjaxResult projectAnalyseChart(StatQuery query) {
        StatChartBarR result = statPartsProgressService.projectAnalyseChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 在制项目数据量
     */
    @GetMapping("/project/count")
    public AjaxResult projectCount(StatQuery query) {
        StatProjectCountR result = statPartsProgressService.projectCount(query);
        return AjaxResult.success(result);
    }

    /**
     * 在制项目零件进度列表
     */
    @GetMapping("/ongoing/parts/list")
    public TableDataInfo ongoingList(StatQuery query) {
        startPage();
        return getDataTable(statPartsProgressService.ongoingList(query));
    }

    /**
     * 在制项目进度
     */
    @GetMapping("/project/progress")
    public AjaxResult projectProgress(StatQuery query) {
        List<StatProjectProgressR> result = statPartsProgressService.projectProgress(query);
        return AjaxResult.success(result);
    }
}
