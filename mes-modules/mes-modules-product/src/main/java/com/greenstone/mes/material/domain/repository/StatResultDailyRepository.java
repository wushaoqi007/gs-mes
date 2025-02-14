package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.domain.entity.StatDataCheckRecord;
import com.greenstone.mes.material.domain.entity.StatDataDaily;
import com.greenstone.mes.material.domain.entity.StatPartsDataSource;
import com.greenstone.mes.material.domain.entity.StatResultDaily;
import com.greenstone.mes.material.infrastructure.util.StatUtil;
import com.greenstone.mes.material.infrastructure.persistence.StatResultDailyDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatResultDailyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatResultDailyRepository {

    private StatResultDailyMapper statResultDailyMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatResultDailyRepository(StatResultDailyMapper statResultDailyMapper, StatisticsTransfer statisticsTransfer) {
        this.statResultDailyMapper = statResultDailyMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public List<StatDataDaily> statDataFromPartStage(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.statDataFromPartStage(statDailyQuery);
    }

    public List<StatDataDaily> selectStatDataDaily(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.selectStatDataDaily(statDailyQuery);
    }

    public List<StatPartsDataSource> selectDailyOweSource(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.selectDailyOweSource(statDailyQuery);
    }

    public List<StatPartsDataSource> selectDailyPlanSource(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.selectDailyOweSource(statDailyQuery);
    }

    public List<StatPartsDataSource> selectDailyFinishSource(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.selectDailyFinishSource(statDailyQuery);
    }

    public List<StatPartsDataSource> selectDailyDeliverySource(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.selectDailyDeliverySource(statDailyQuery);
    }

    public List<StatDataDaily> statDataFromStockRecord(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.statDataFromStockRecord(statDailyQuery);
    }

    public List<StatDataCheckRecord> statDataFromCheckRecord(StatDailyQuery statDailyQuery) {
        return statResultDailyMapper.statDataFromCheckRecord(statDailyQuery);
    }

    public void saveStatResultDaily(List<StatResultDaily> statResultDailyList, String todayStr) {
        log.info("保存日统计数据，size{}", statResultDailyList.size());
        if (CollUtil.isNotEmpty(statResultDailyList)) {
            List<StatResultDailyDO> statResultDailyDOList = statisticsTransfer.toStatResultDailyDoList(statResultDailyList);
            LambdaQueryWrapper<StatResultDailyDO> queryWrapper = Wrappers.lambdaQuery(StatResultDailyDO.class);
            // 今天的统计，删除今天及之后的；不是今天的计算，只删除当天的
            if (StatUtil.dateToSimpleStr(new Date()).equals(todayStr)) {
                queryWrapper.ge(StatResultDailyDO::getStatisticDate, todayStr);
            } else {
                queryWrapper.eq(StatResultDailyDO::getStatisticDate, todayStr);
            }
            statResultDailyMapper.delete(queryWrapper);
            statResultDailyMapper.insertBatchSomeColumn(statResultDailyDOList);
        }
    }

    public List<StatResultDaily> selectStatResultList(StatDailyQuery query) {
        LambdaQueryWrapper<StatResultDailyDO> queryWrapper = Wrappers.lambdaQuery(StatResultDailyDO.class)
                .ge(query.getStartTime() != null, StatResultDailyDO::getStatisticDate, query.getStartTime())
                .le(query.getEndTime() != null, StatResultDailyDO::getStatisticDate, query.getEndTime())
                .eq(StrUtil.isNotEmpty(query.getProvider()), StatResultDailyDO::getProvider, query.getProvider())
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatResultDailyDO::getProjectCode, query.getProjectCode());
        List<StatResultDailyDO> list = statResultDailyMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatResultDailyList(list);
    }
}
