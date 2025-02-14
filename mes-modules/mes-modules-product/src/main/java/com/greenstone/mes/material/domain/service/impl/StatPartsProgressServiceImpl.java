package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.application.assembler.StatBoardAssembler;
import com.greenstone.mes.material.application.assembler.StatBoardChartAssembler;
import com.greenstone.mes.material.domain.entity.StatDataParts;
import com.greenstone.mes.material.domain.entity.StatPartsProgress;
import com.greenstone.mes.material.domain.service.StatPartsProgressService;
import com.greenstone.mes.material.dto.cmd.StatProgressCmd;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.domain.repository.StatPartsProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:49
 */
@Slf4j
@Service
public class StatPartsProgressServiceImpl implements StatPartsProgressService {

    private final StatPartsProgressRepository statPartsProgressRepository;
    private final StatBoardAssembler statBoardAssembler;
    private final StatBoardChartAssembler statBoardChartAssembler;

    public StatPartsProgressServiceImpl(StatPartsProgressRepository statPartsProgressRepository, StatBoardAssembler statBoardAssembler,
                                        StatBoardChartAssembler statBoardChartAssembler) {
        this.statPartsProgressRepository = statPartsProgressRepository;
        this.statBoardAssembler = statBoardAssembler;
        this.statBoardChartAssembler = statBoardChartAssembler;
    }

    @Override
    public void partsProgressStatistics(StatProgressCmd statProgressCmd) {
        // 查询数据
        List<StatDataParts> statDataPartsList = statPartsProgressRepository.statDataForOngoingParts(getTimeQuery(statProgressCmd));
        // 统计数据
        List<StatPartsProgress> statPartsProgressList = statPartsProgress(statDataPartsList);
        // 保存
        statPartsProgressRepository.save(statPartsProgressList);
    }

    @Override
    public List<StatProjectAnalyseR> projectAnalyse(StatQuery query) {
        List<StatPartsProgress> statPartsProgressList = statPartsProgressRepository.selectOngoingList(query);
        return statBoardAssembler.toStatProjectAnalyseR(statPartsProgressList);
    }

    @Override
    public StatChartBarR projectAnalyseChart(StatQuery query) {
        List<StatProjectAnalyseR> statProjectAnalyseRList = projectAnalyse(query);
        return statBoardChartAssembler.toProjectAnalyseChartR(statProjectAnalyseRList);
    }

    @Override
    public StatProjectCountR projectCount(StatQuery query) {
        List<StatPartsProgress> statPartsProgressList = statPartsProgressRepository.selectOngoingList(query);
        return statBoardAssembler.toStatProjectCountR(statPartsProgressList);
    }

    @Override
    public List<StatPartsProgressR> ongoingList(StatQuery query) {
        List<StatPartsProgress> statPartsProgressList = statPartsProgressRepository.selectOngoingList(query);
        return statBoardAssembler.toStatPartsProgressRList(statPartsProgressList);
    }

    @Override
    public List<StatProjectProgressR> projectProgress(StatQuery query) {
        List<StatPartsProgress> statPartsProgressList = statPartsProgressRepository.selectList(query);
        return statBoardAssembler.toStatProjectProgressR(statPartsProgressList);
    }


    public List<StatPartsProgress> statPartsProgress(List<StatDataParts> statDataPartsList) {
        List<StatPartsProgress> result = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.##%");
        Map<Long, List<StatDataParts>> groupByDetailId = statDataPartsList.stream().collect(Collectors.groupingBy(StatDataParts::getWorksheetDetailId));
        groupByDetailId.forEach((detailId, list) -> {
            String deliverRate;
            String finishedRate = "0%";
            // 计算交付率=待收件阶段出库量/待收件阶段入库量
            Optional<StatDataParts> findWaitReceive = list.stream().filter(a -> a.getStage() == 1).findFirst();
            if (findWaitReceive.isPresent()) {
                StatDataParts waitReceivedPart = findWaitReceive.get();
                int deliverPartNum = waitReceivedPart.getOutStockTotal() != null ? waitReceivedPart.getOutStockTotal() : 0;
                int deliverPaperNum = deliverPartNum > 0 ? waitReceivedPart.getPaperNum() : 0;
                int finishedPartNum = 0;
                int finishedPaperNum = 0;
                deliverRate = decimalFormat.format((double) deliverPartNum / (double) waitReceivedPart.getInStockTotal());
                // 计算入库率=良品阶段入库量/待收件阶段入库量
                if (list.size() > 1) {
                    Optional<StatDataParts> findFinished = list.stream().filter(a -> a.getStage() == 9).findFirst();
                    if (findFinished.isPresent()) {
                        StatDataParts finishedPart = findFinished.get();
                        finishedPartNum = finishedPart.getInStockTotal() != null ? finishedPart.getInStockTotal() : 0;
                        finishedPaperNum = finishedPartNum > 0 ? finishedPart.getPaperNum() : 0;
                        finishedRate = decimalFormat.format((double) finishedPartNum / (double) waitReceivedPart.getInStockTotal());
                    }
                }
                StatPartsProgress statPartsProgress = StatPartsProgress.builder().worksheetDetailId(detailId).projectCode(waitReceivedPart.getProjectCode())
                        .customerName(waitReceivedPart.getCustomerName()).customerShortName(waitReceivedPart.getCustomerShortName())
                        .deliverRate(deliverRate).finishedRate(finishedRate)
                        .deliverPartNum(deliverPartNum).deliverPaperNum(deliverPaperNum)
                        .finishedPartNum(finishedPartNum).finishedPaperNum(finishedPaperNum)
                        .componentCode(waitReceivedPart.getComponentCode()).componentName(waitReceivedPart.getComponentName())
                        .uploadTime(waitReceivedPart.getUploadTime()).paperNum(waitReceivedPart.getPaperNum()).partNum(waitReceivedPart.getInStockTotal())
                        .remark(waitReceivedPart.getRemark())
                        .build();
                result.add(statPartsProgress);
            }
        });
        return result;
    }

    public StatDailyQuery getTimeQuery(StatProgressCmd statProgressCmd) {
        if (StrUtil.isEmpty(statProgressCmd.getStartTime())) {
            if (statProgressCmd.getTime() == null || StrUtil.isEmpty(statProgressCmd.getUnit())) {
                statProgressCmd.setTime(1);
                statProgressCmd.setUnit("day");
            }
            statProgressCmd.setStartTime(StatUtil.dateToCompleteStr(StatUtil.timeBeforeInterval(new Date(), statProgressCmd.getTime(), statProgressCmd.getUnit())));
        }
        if (StrUtil.isEmpty(statProgressCmd.getEndTime())) {
            statProgressCmd.setEndTime(StatUtil.dateToCompleteStr(new Date()));
        }
        return StatDailyQuery.builder().startTime(statProgressCmd.getStartTime()).endTime(statProgressCmd.getEndTime()).build();
    }
}
