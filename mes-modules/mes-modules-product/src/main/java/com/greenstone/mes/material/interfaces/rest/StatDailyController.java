package com.greenstone.mes.material.interfaces.rest;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.common.core.web.page.TableDataInfo;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.dto.cmd.StatDailyCmd;
import com.greenstone.mes.material.domain.service.StatResultDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日统计类接口
 *
 * @author wushaoqi
 * @date 2023-02-27-15:00
 */
@Slf4j
@RestController
@RequestMapping("/stat/daily")
public class StatDailyController extends BaseController {
    private final StatResultDailyService statResultDailyService;

    public StatDailyController(StatResultDailyService statResultDailyService) {
        this.statResultDailyService = statResultDailyService;
    }

    /**
     * 日统计
     */
    @PostMapping
    public AjaxResult dailyStat(@RequestBody(required = false) StatDailyCmd statDailyCmd) {
        log.info("stat daily start");
        statResultDailyService.dailyStatistics(statDailyCmd);
        return AjaxResult.success("日统计,计算中...");
    }

    /**
     * 获取日欠货的加工商
     */
    @GetMapping("/owe/provider")
    public AjaxResult listOweProvider(StatQuery query) {
        List<String> list = statResultDailyService.listOweProvider(query);
        return AjaxResult.success(list);
    }

    /**
     * 日欠货数据汇总
     */
    @GetMapping("/owe")
    public AjaxResult selectDailyOwe(StatQuery query) {
        List<StatDailyR> result = statResultDailyService.selectDailyOwe(query);
        return AjaxResult.success(result);
    }

    /**
     * 日欠货数据汇总（图）
     */
    @GetMapping("/owe/chart")
    public AjaxResult selectDailyOweChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyOweChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 欠货数据来源
     */
    @GetMapping("/owe/source")
    public TableDataInfo selectDailyOweSource(StatQuery query) {
        startPage();
        List<StatPartsDataSourceR> result = statResultDailyService.selectDailyOweSource(query);
        return getDataTable(result);
    }


    /**
     * 日欠货数据分布
     */
    @GetMapping("/owe/distribution")
    public AjaxResult selectDailyOweDistribution(StatQuery query) {
        List<StatDailyR> result = statResultDailyService.selectDailyOweDistribution(query);
        return AjaxResult.success(result);
    }

    /**
     * 日欠货数据分布（图）
     */
    @GetMapping("/owe/distribution/chart")
    public AjaxResult selectDailyOweDistributionChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyOweDistributionChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 日交货数据汇总
     */
    @GetMapping("/delivery")
    public AjaxResult selectDailyDelivery(StatQuery query) {
        List<StatDailyR> result = statResultDailyService.selectDailyDelivery(query);
        return AjaxResult.success(result);
    }

    /**
     * 日交货数据汇总（图）
     */
    @GetMapping("/delivery/chart")
    public AjaxResult selectDailyDeliveryChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyDeliveryChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 日交货数据来源
     */
    @GetMapping("/delivery/source")
    public TableDataInfo selectDailyDeliverySource(StatQuery query) {
        startPage();
        List<StatPartsDataSourceR> result = statResultDailyService.selectDailyDeliverySource(query);
        return getDataTable(result);
    }

    /**
     * 日完成数据汇总
     */
    @GetMapping("/finish")
    public AjaxResult selectDailyFinish(StatQuery query) {
        List<StatDailyFinishR> result = statResultDailyService.selectDailyFinish(query);
        return AjaxResult.success(result);
    }

    /**
     * 日完成数据汇总（图）
     */
    @GetMapping("/finish/chart")
    public AjaxResult selectDailyFinishChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyFinishChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 日完成数据分布（图）
     */
    @GetMapping("/finish/distribution/chart")
    public AjaxResult selectDailyFinishDistributionChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyFinishDistributionChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 日完成数据来源
     */
    @GetMapping("/finish/source")
    public TableDataInfo selectDailyFinishSource(StatQuery query) {
        startPage();
        List<StatPartsDataSourceR> result = statResultDailyService.selectDailyFinishSource(query);
        return getDataTable(result);
    }

    /**
     * 待加工数据汇总
     */
    @GetMapping("/plan")
    public AjaxResult selectDailyPlan(StatQuery query) {
        List<StatDailyR> result = statResultDailyService.selectDailyPlan(query);
        return AjaxResult.success(result);
    }

    /**
     * 待加工数据汇总（图）
     */
    @GetMapping("/plan/chart")
    public AjaxResult selectDailyPlanChart(StatQuery query) {
        StatChartBarR result = statResultDailyService.selectDailyPlanChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 待加工数据来源
     */
    @GetMapping("/plan/source")
    public TableDataInfo selectDailyPlanSource(StatQuery query) {
        startPage();
        List<StatPartsDataSourceR> result = statResultDailyService.selectDailyPlanSource(query);
        return getDataTable(result);
    }
}
