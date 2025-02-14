package com.greenstone.mes.material.interfaces.rest;

import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.domain.service.StatResultDesignerService;
import com.greenstone.mes.material.domain.service.StatResultMonthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 月统计类接口
 *
 * @author wushaoqi
 * @date 2023-02-27-15:00
 */
@Slf4j
@RestController
@RequestMapping("/stat/month")
public class StatMonthController extends BaseController {
    private final StatResultMonthService statResultMonthService;
    private final StatResultDesignerService statResultDesignerService;

    public StatMonthController(StatResultMonthService statResultMonthService,
                               StatResultDesignerService statResultDesignerService) {
        this.statResultMonthService = statResultMonthService;
        this.statResultDesignerService = statResultDesignerService;
    }


    /**
     * 月统计
     */
    @PostMapping
    public AjaxResult monthStat() {
        log.info("stat month start");
        statResultMonthService.monthStatistics();
        return AjaxResult.success("月统计,计算中...");
    }

    /**
     * 设计出图月统计
     */
    @PostMapping("/designer")
    public AjaxResult designerStat() {
        log.info("stat month designer start");
        statResultDesignerService.monthStatistics();
        return AjaxResult.success("设计出图月统计,计算中...");
    }

    /**
     * 月欠货数据分析
     */
    @GetMapping("/owe")
    public AjaxResult selectMonthOwe(StatMonthQuery query) {
        List<StatMonthR> result = statResultMonthService.selectMonthOwe(query);
        return AjaxResult.success(result);
    }

    /**
     * 月欠货数据分析（图）
     */
    @GetMapping("/owe/chart")
    public AjaxResult selectMonthOweChart(StatMonthQuery query) {
        StatChartBarR result = statResultMonthService.selectMonthOweChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 年欠货数据分析（图）
     */
    @GetMapping("/year/owe/chart")
    public AjaxResult selectYearOweChart(StatMonthQuery query) {
        StatChartBarR result = statResultMonthService.selectYearOweChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月不良类型汇总
     */
    @GetMapping("/rework/type")
    public AjaxResult selectReworkType(StatMonthQuery query) {
        List<StatReworkTypeR> result = statResultMonthService.selectReworkType(query);
        return AjaxResult.success(result);
    }

    /**
     * 月不良类型汇总（图）
     */
    @GetMapping("/rework/type/chart")
    public AjaxResult selectReworkTypeChart(StatMonthQuery query) {
        StatChartBarAndLineR result = statResultMonthService.selectReworkTypeChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月不良率汇总
     */
    @GetMapping("/rework/rate")
    public AjaxResult selectReworkRate(StatMonthQuery query) {
        List<StatMonthReworkR> result = statResultMonthService.selectReworkRate(query);
        return AjaxResult.success(result);
    }

    /**
     * 月不良率汇总（图）
     */
    @GetMapping("/rework/rate/chart")
    public AjaxResult selectReworkRateChart(StatMonthQuery query) {
        StatChartBarR result = statResultMonthService.selectReworkRateChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月送检数量汇总
     */
    @GetMapping("/check")
    public AjaxResult selectMonthCheck(StatMonthQuery query) {
        List<StatMonthCheckR> result = statResultMonthService.selectMonthCheck(query);
        return AjaxResult.success(result);
    }

    /**
     * 月送检数量汇总（图）
     */
    @GetMapping("/check/chart")
    public AjaxResult selectMonthCheckChart(StatMonthQuery query) {
        StatChartBarR result = statResultMonthService.selectMonthCheckChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月加工件(计划)数据汇总
     */
    @GetMapping("/plan")
    public AjaxResult selectMonthPlan(StatMonthQuery query) {
        List<StatMonthPlanR> result = statResultMonthService.selectMonthPlan(query);
        return AjaxResult.success(result);
    }

    /**
     * 月加工件(计划)数据汇总（图）
     */
    @GetMapping("/plan/chart")
    public AjaxResult selectMonthPlanChart(StatMonthQuery query) {
        StatChartBarAndLineR result = statResultMonthService.selectMonthPlanChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月出图数据汇总
     */
    @GetMapping("/import")
    public AjaxResult selectMonthImport(StatMonthQuery query) {
        List<StatMonthPlanR> result = statResultDesignerService.selectMonthImport(query);
        return AjaxResult.success(result);
    }

    /**
     * 月出图数据汇总（图）
     */
    @GetMapping("/import/chart")
    public AjaxResult selectMonthImportChart(StatMonthQuery query) {
        StatChartBarR result = statResultDesignerService.selectMonthImportChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月出图超期数据汇总
     */
    @GetMapping("/import/overdue")
    public AjaxResult selectMonthImportOverdue(StatMonthQuery query) {
        List<StatMonthDesignerOverdueR> result = statResultDesignerService.selectMonthDesignerOverdue(query);
        return AjaxResult.success(result);
    }

    /**
     * 月出图超期数据汇总（图）
     */
    @GetMapping("/import/overdue/chart")
    public AjaxResult selectMonthImportOverdueChart(StatMonthQuery query) {
        StatChartBarAndLineR result = statResultDesignerService.selectMonthImportOverdueChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

    /**
     * 月特殊件数据汇总
     */
    @GetMapping("/special")
    public AjaxResult selectMonthSpecial(StatMonthQuery query) {
        List<StatMonthSpecialR> result = statResultDesignerService.selectMonthSpecial(query);
        return AjaxResult.success(result);
    }

    /**
     * 月特殊件数据汇总（图）
     */
    @GetMapping("/special/chart")
    public AjaxResult selectMonthSpecialChart(StatMonthQuery query) {
        StatChartBarAndLineR result = statResultDesignerService.selectMonthSpecialChart(query);
        return AjaxResult.success(JSONObject.parseObject(JSONObject.toJSONString(result)));
    }

}
