package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatWeekQuery;
import com.greenstone.mes.material.domain.entity.StatResultWeek;
import com.greenstone.mes.material.infrastructure.persistence.StatResultWeekDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatResultWeekMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatResultWeekRepository {

    private StatResultWeekMapper statResultWeekMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatResultWeekRepository(StatResultWeekMapper statResultWeekMapper, StatisticsTransfer statisticsTransfer) {
        this.statResultWeekMapper = statResultWeekMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public void save(List<StatResultWeek> statResultWeekList) {
        log.info("保存周统计数据，size{}", statResultWeekList.size());
        if (CollUtil.isNotEmpty(statResultWeekList)) {
            List<StatResultWeekDO> statResultWeekDOList = statisticsTransfer.toStatResultWeekDOList(statResultWeekList);
            QueryWrapper<StatResultWeekDO> queryWrapper = Wrappers.query(StatResultWeekDO.builder()
                    .statisticMonth(statResultWeekDOList.get(0).getStatisticMonth()).monthWeek(statResultWeekDOList.get(0).getMonthWeek()).build());
            statResultWeekMapper.delete(queryWrapper);
            statResultWeekMapper.insertBatchSomeColumn(statResultWeekDOList);
        }
    }

    public List<StatResultWeek> selectStatResultList(StatWeekQuery query) {
        LambdaQueryWrapper<StatResultWeekDO> queryWrapper = Wrappers.lambdaQuery(StatResultWeekDO.class)
                .ge(query.getMonthStart() != null, StatResultWeekDO::getStatisticMonth, query.getMonthStart())
                .le(query.getMonthEnd() != null, StatResultWeekDO::getStatisticMonth, query.getMonthEnd())
                .eq(StrUtil.isNotEmpty(query.getProvider()), StatResultWeekDO::getProvider, query.getProvider())
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatResultWeekDO::getProjectCode, query.getProjectCode());
        List<StatResultWeekDO> list = statResultWeekMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatResultWeekList(list);
    }
}
