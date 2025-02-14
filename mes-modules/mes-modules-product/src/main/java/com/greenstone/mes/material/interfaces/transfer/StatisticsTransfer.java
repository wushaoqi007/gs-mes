package com.greenstone.mes.material.interfaces.transfer;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.domain.entity.*;
import com.greenstone.mes.material.infrastructure.persistence.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-01-09-10:39
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface StatisticsTransfer {

    StatResultDailyDO toStatResultDailyDo(StatResultDaily statResultDaily);

    List<StatResultDailyDO> toStatResultDailyDoList(List<StatResultDaily> statResultDailyList);


    StatResultDaily toStatResultDaily(StatResultDailyDO statResultDailyDO);

    List<StatResultDaily> toStatResultDailyList(List<StatResultDailyDO> statResultDailyDOList);


    StatResultMonthDO toStatResultMonthDo(StatResultMonth statResultMonth);

    List<StatResultMonthDO> toStatResultMonthDoList(List<StatResultMonth> statResultMonthList);


    StatResultMonth toStatResultMonth(StatResultMonthDO statResultMonthDO);

    List<StatResultMonth> toStatResultMonthList(List<StatResultMonthDO> statResultMonthDOList);


    StatResultWeekDO toStatResultWeekDO(StatResultWeek statResultWeek);

    List<StatResultWeekDO> toStatResultWeekDOList(List<StatResultWeek> statResultWeekList);


    StatResultWeek toStatResultWeek(StatResultWeekDO statResultWeekDO);
    List<StatResultWeek> toStatResultWeekList(List<StatResultWeekDO> statResultWeekDOList);


    StatResultReworkDO toStatResultReworkDO(StatResultRework statResultRework);

    List<StatResultReworkDO> toStatResultReworkDOList(List<StatResultRework> statResultReworkList);


    StatResultRework toStatResultRework(StatResultReworkDO statResultReworkDO);

    List<StatResultRework> toStatResultReworkList(List<StatResultReworkDO> statResultReworkDOList);


    StatPartsProgressDO toStatPartProgressDO(StatPartsProgress statPartsProgress);

    List<StatPartsProgressDO> toStatPartProgressDOList(List<StatPartsProgress> statPartsProgress);


    StatPartsProgress toStatPartProgress(StatPartsProgressDO statPartsProgressDO);
    List<StatPartsProgress> toStatPartProgressList(List<StatPartsProgressDO> statPartsProgressDOList);


    StatResultDesignerDO toStatResultDesignerDO(StatResultDesigner statResultDesigner);

    List<StatResultDesignerDO> toStatResultDesignerDOList(List<StatResultDesigner> statResultDesignerList);


    StatResultDesigner toStatResultDesigner(StatResultDesignerDO statResultDesignerDO);
    List<StatResultDesigner> toStatResultDesignerList(List<StatResultDesignerDO> statResultDesignerDOList);

}
