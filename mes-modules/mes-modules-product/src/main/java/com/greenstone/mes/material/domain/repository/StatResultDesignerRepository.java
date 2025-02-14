package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatMonthQuery;
import com.greenstone.mes.material.domain.entity.StatDataDesigner;
import com.greenstone.mes.material.domain.entity.StatResultDesigner;
import com.greenstone.mes.material.infrastructure.persistence.StatResultDesignerDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatResultDesignerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatResultDesignerRepository {

    private StatResultDesignerMapper statResultDesignerMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatResultDesignerRepository(StatResultDesignerMapper statResultDesignerMapper, StatisticsTransfer statisticsTransfer) {
        this.statResultDesignerMapper = statResultDesignerMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public List<StatDataDesigner> statDataForDesigner(StatDailyQuery monthQuery) {
        return statResultDesignerMapper.statDataForDesigner(monthQuery);
    }

    public void save(List<StatResultDesigner> statResultDesignerList) {
        log.info("保存设计月统计数据,size{}", statResultDesignerList.size());
        if(CollUtil.isNotEmpty(statResultDesignerList)){
            List<StatResultDesignerDO> statResultDesignerDOList = statisticsTransfer.toStatResultDesignerDOList(statResultDesignerList);
            QueryWrapper<StatResultDesignerDO> queryWrapper = Wrappers.query(StatResultDesignerDO.builder().statisticMonth(statResultDesignerDOList.get(0).getStatisticMonth()).build());
            statResultDesignerMapper.delete(queryWrapper);
            statResultDesignerMapper.insertBatchSomeColumn(statResultDesignerDOList);
        }
    }

    public List<StatResultDesigner> selectStatResultList(StatMonthQuery query) {
        LambdaQueryWrapper<StatResultDesignerDO> queryWrapper = Wrappers.lambdaQuery(StatResultDesignerDO.class)
                .ge(query.getMonthStart() != null, StatResultDesignerDO::getStatisticMonth, query.getMonthStart())
                .le(query.getMonthEnd() != null, StatResultDesignerDO::getStatisticMonth, query.getMonthEnd())
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatResultDesignerDO::getProjectCode, query.getProjectCode());
        List<StatResultDesignerDO> list = statResultDesignerMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatResultDesignerList(list);
    }
}
