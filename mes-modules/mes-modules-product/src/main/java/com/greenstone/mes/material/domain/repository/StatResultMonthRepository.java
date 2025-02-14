package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.domain.entity.StatResultMonth;
import com.greenstone.mes.material.infrastructure.persistence.StatResultMonthDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatResultMonthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatResultMonthRepository {

    private StatResultMonthMapper statResultMonthMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatResultMonthRepository(StatResultMonthMapper statResultMonthMapper, StatisticsTransfer statisticsTransfer) {
        this.statResultMonthMapper = statResultMonthMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public void save(List<StatResultMonth> statResultMonthList) {
        log.info("保存月统计数据，size{}", statResultMonthList.size());
        if (CollUtil.isNotEmpty(statResultMonthList)) {
            List<StatResultMonthDO> statResultMonthDOList = statisticsTransfer.toStatResultMonthDoList(statResultMonthList);
            QueryWrapper<StatResultMonthDO> queryWrapper = Wrappers.query(StatResultMonthDO.builder().statisticMonth(statResultMonthList.get(0).getStatisticMonth()).build());
            statResultMonthMapper.delete(queryWrapper);
            statResultMonthMapper.insertBatchSomeColumn(statResultMonthDOList);
        }
    }

    public List<StatResultMonth> selectStatResultList(StatMonthQuery query) {
        LambdaQueryWrapper<StatResultMonthDO> queryWrapper = Wrappers.lambdaQuery(StatResultMonthDO.class)
                .ge(query.getMonthStart() != null, StatResultMonthDO::getStatisticMonth, query.getMonthStart())
                .le(query.getMonthEnd() != null, StatResultMonthDO::getStatisticMonth, query.getMonthEnd())
                .eq(StrUtil.isNotEmpty(query.getProvider()), StatResultMonthDO::getProvider, query.getProvider())
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatResultMonthDO::getProjectCode, query.getProjectCode());
        List<StatResultMonthDO> list = statResultMonthMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatResultMonthList(list);
    }
}
