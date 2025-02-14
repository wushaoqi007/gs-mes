package com.greenstone.mes.material.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.application.dto.result.*;
import com.greenstone.mes.material.application.assembler.StatMonthAssembler;
import com.greenstone.mes.material.application.assembler.StatMonthChartAssembler;
import com.greenstone.mes.material.domain.entity.Project;
import com.greenstone.mes.material.domain.entity.StatDataDesigner;
import com.greenstone.mes.material.domain.entity.StatResultDesigner;
import com.greenstone.mes.material.domain.service.StatResultDesignerService;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.domain.repository.ProjectRepository;
import com.greenstone.mes.material.domain.repository.StatResultDesignerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2023-02-23-8:49
 */
@Slf4j
@Service
public class StatResultDesignerServiceImpl implements StatResultDesignerService {

    private final StatResultDesignerRepository statResultDesignerRepository;
    private final StatMonthAssembler statMonthAssembler;
    private final ProjectRepository projectRepository;
    private final StatMonthChartAssembler statMonthChartAssembler;

    public StatResultDesignerServiceImpl(StatResultDesignerRepository statResultDesignerRepository, StatMonthAssembler statMonthAssembler,
                                         ProjectRepository projectRepository, StatMonthChartAssembler statMonthChartAssembler) {
        this.statResultDesignerRepository = statResultDesignerRepository;
        this.statMonthAssembler = statMonthAssembler;
        this.projectRepository = projectRepository;
        this.statMonthChartAssembler = statMonthChartAssembler;
    }

    @Override
    public void monthStatistics() {
        // 查询设计出图数据
        List<StatDataDesigner> statDataDesignerList = statResultDesignerRepository.statDataForDesigner(getMonthQuery());
        // 统计
        List<StatResultDesigner> statResultDesignerList = monthStat(statDataDesignerList);
        // 保存
        statResultDesignerRepository.save(statResultDesignerList);
    }

    @Override
    public List<StatMonthPlanR> selectMonthImport(StatMonthQuery query) {
        // 查询
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultDesigner> statResultDesignerList = statResultDesignerRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthPlanROfImport(statResultDesignerList, monthQuery);
    }

    @Override
    public StatChartBarR selectMonthImportChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthPlanR> statMonthPlanRList = selectMonthImport(query);
        return statMonthChartAssembler.toImportChartR(statMonthPlanRList, query);
    }

    @Override
    public List<StatMonthDesignerOverdueR> selectMonthDesignerOverdue(StatMonthQuery query) {
        // 查询设计月统计
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultDesigner> statResultDesignerList = statResultDesignerRepository.selectStatResultList(monthQuery);
        // 查询计划出图项目
        List<Project> projectList = projectRepository.listByDesignDeadlineInOneYear(monthQuery.getYear(), query.getProjectCode());
        return statMonthAssembler.toMonthDesignerOverdueR(statResultDesignerList, projectList, monthQuery);
    }

    @Override
    public StatChartBarAndLineR selectMonthImportOverdueChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthDesignerOverdueR> statMonthDesignerOverdueRList = selectMonthDesignerOverdue(query);
        return statMonthChartAssembler.toImportOverdueChartR(statMonthDesignerOverdueRList, query);
    }

    @Override
    public List<StatMonthSpecialR> selectMonthSpecial(StatMonthQuery query) {
        // 查询设计月统计
        StatMonthQuery monthQuery = statMonthAssembler.toStatYearMonthQuery(query);
        List<StatResultDesigner> statResultDesignerList = statResultDesignerRepository.selectStatResultList(monthQuery);
        return statMonthAssembler.toMonthSpecialR(statResultDesignerList, monthQuery);
    }

    @Override
    public StatChartBarAndLineR selectMonthSpecialChart(StatMonthQuery query) {
        // 默认查询本年
        if (StrUtil.isEmpty(query.getYear())) {
            query.setYear(StatUtil.toYearStr(new Date()));
        }
        List<StatMonthSpecialR> statMonthSpecialRList = selectMonthSpecial(query);
        return statMonthChartAssembler.toSpecialChartR(statMonthSpecialRList, query);
    }

    public List<StatResultDesigner> monthStat(List<StatDataDesigner> statDataDesignerList) {
        List<StatResultDesigner> statResultDesignerList = new ArrayList<>();
        Map<String, List<StatDataDesigner>> groupByProjectCode = statDataDesignerList.stream().collect(Collectors.groupingBy(StatDataDesigner::getProjectCode));
        groupByProjectCode.forEach((projectCode, list) -> {
            int partTotal = 0;
            int paperTotal = 0;
            int partUpdateTotal = 0;
            int paperUpdateTotal = 0;
            int partUrgentTotal = 0;
            int paperUrgentTotal = 0;
            int partRepairTotal = 0;
            int paperRepairTotal = 0;
            for (StatDataDesigner statDataDesigner : list) {
                partTotal += statDataDesigner.getPartNumber() == null ? 0 : statDataDesigner.getPartNumber();
                paperTotal += statDataDesigner.getPaperNumber() == null ? 0 : statDataDesigner.getPaperNumber();
                if ("是".equals(statDataDesigner.getFastParts())) {
                    partUrgentTotal += statDataDesigner.getPartNumber() == null ? 0 : statDataDesigner.getPartNumber();
                    paperUrgentTotal += statDataDesigner.getPaperNumber() == null ? 0 : statDataDesigner.getPaperNumber();
                }
                if (!Objects.isNull(statDataDesigner.getUpdateParts()) && statDataDesigner.getUpdateParts()) {
                    partUpdateTotal += statDataDesigner.getPartNumber() == null ? 0 : statDataDesigner.getPartNumber();
                    paperUpdateTotal += statDataDesigner.getPaperNumber() == null ? 0 : statDataDesigner.getPaperNumber();
                }
                if (!Objects.isNull(statDataDesigner.getRepairParts()) && statDataDesigner.getRepairParts()) {
                    partRepairTotal += statDataDesigner.getPartNumber() == null ? 0 : statDataDesigner.getPartNumber();
                    paperRepairTotal += statDataDesigner.getPaperNumber() == null ? 0 : statDataDesigner.getPaperNumber();
                }
            }
            // 是否超期
            boolean isOverdue = false;
            int overdueDays = 0;
            if (!Objects.isNull(list.get(0).getDesignDeadline()) && list.get(0).getDesignDeadline().getTime() < list.get(0).getCreateTime().getTime()) {
                isOverdue = true;
                overdueDays = (int) Math.ceil(((double) (list.get(0).getCreateTime().getTime()) - (double) (list.get(0).getDesignDeadline().getTime())) / (1000 * 3600 * 24));
            }


            StatResultDesigner statResultDesigner = StatResultDesigner.builder().projectCode(projectCode)
                    .partTotal(partTotal).paperTotal(paperTotal)
                    .partUpdateTotal(partUpdateTotal).paperUpdateTotal(paperUpdateTotal)
                    .partRepairTotal(partRepairTotal).paperRepairTotal(paperRepairTotal)
                    .partUrgentTotal(partUrgentTotal).paperUrgentTotal(paperUrgentTotal)
                    .overdue(isOverdue).overdueDays(overdueDays)
                    .statisticDate(StatUtil.dateToSimpleStr(new Date())).statisticMonth(StatUtil.monthToStr(new Date())).build();
            statResultDesignerList.add(statResultDesigner);

        });
        return statResultDesignerList;
    }

    public StatDailyQuery getMonthQuery() {
        return StatDailyQuery.builder().startTime(StatUtil.dateToSimpleStr(StatUtil.monthStart(new Date()))).endTime(StatUtil.dateToSimpleStr(StatUtil.monthEnd(new Date()))).build();
    }
}
