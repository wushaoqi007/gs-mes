package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.domain.entity.StatResultRework;
import com.greenstone.mes.material.infrastructure.persistence.StatResultReworkDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatResultReworkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatResultReworkRepository {

    private StatResultReworkMapper statResultReworkMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatResultReworkRepository(StatResultReworkMapper statResultReworkMapper, StatisticsTransfer statisticsTransfer) {
        this.statResultReworkMapper = statResultReworkMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public void saveStatResultRework(List<StatResultRework> statResultReworkList, String todayStr) {
        log.info("保存每日不良类型统计数据，size{}", statResultReworkList.size());
        if (CollUtil.isNotEmpty(statResultReworkList)) {
            List<StatResultReworkDO> statResultReworkDOList = statisticsTransfer.toStatResultReworkDOList(statResultReworkList);
            QueryWrapper<StatResultReworkDO> queryWrapper = Wrappers.query(StatResultReworkDO.builder().statisticDate(todayStr).build());
            statResultReworkMapper.delete(queryWrapper);
            statResultReworkMapper.insertBatchSomeColumn(statResultReworkDOList);
        }
    }

    public List<StatResultRework> selectStatResultReworkList(StatDailyQuery query) {
        LambdaQueryWrapper<StatResultReworkDO> queryWrapper = Wrappers.lambdaQuery(StatResultReworkDO.class)
                .ge(query.getStartTime() != null, StatResultReworkDO::getStatisticDate, query.getStartTime())
                .le(query.getEndTime() != null, StatResultReworkDO::getStatisticDate, query.getEndTime())
                .eq(StrUtil.isNotEmpty(query.getProvider()), StatResultReworkDO::getProvider, query.getProvider())
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatResultReworkDO::getProjectCode, query.getProjectCode());
        List<StatResultReworkDO> list = statResultReworkMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatResultReworkList(list);
    }
}
