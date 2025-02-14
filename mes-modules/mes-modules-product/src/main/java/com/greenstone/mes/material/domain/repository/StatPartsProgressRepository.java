package com.greenstone.mes.material.domain.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.application.dto.StatQuery;
import com.greenstone.mes.material.domain.entity.StatDataParts;
import com.greenstone.mes.material.domain.entity.StatPartsProgress;
import com.greenstone.mes.material.infrastructure.persistence.StatPartsProgressDO;
import com.greenstone.mes.material.interfaces.transfer.StatisticsTransfer;
import com.greenstone.mes.material.infrastructure.mapper.StatPartsProgressMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Slf4j
@Service
public class StatPartsProgressRepository {

    private StatPartsProgressMapper statPartsProgressMapper;
    private StatisticsTransfer statisticsTransfer;


    public StatPartsProgressRepository(StatPartsProgressMapper statPartsProgressMapper, StatisticsTransfer statisticsTransfer) {
        this.statPartsProgressMapper = statPartsProgressMapper;
        this.statisticsTransfer = statisticsTransfer;
    }

    public List<StatDataParts> statDataForOngoingParts(StatDailyQuery statDailyQuery) {
        return statPartsProgressMapper.statDataForOngoingParts(statDailyQuery);
    }

    public void save(List<StatPartsProgress> statPartsProgressList) {
        log.info("保存零件进度统计数据,size{}", statPartsProgressList.size());
        if (CollUtil.isNotEmpty(statPartsProgressList)) {
            List<StatPartsProgressDO> statResultDesignerDOList = statisticsTransfer.toStatPartProgressDOList(statPartsProgressList);
            for (StatPartsProgressDO statPartsProgressDO : statResultDesignerDOList) {
                QueryWrapper<StatPartsProgressDO> queryWrapper = Wrappers.query(StatPartsProgressDO.builder().worksheetDetailId(statPartsProgressDO.getWorksheetDetailId()).build());
                statPartsProgressMapper.delete(queryWrapper);
            }
            statPartsProgressMapper.insertBatchSomeColumn(statResultDesignerDOList);
        }
    }

    /**
     * 查询在制零件进度
     */
    public List<StatPartsProgress> selectOngoingList(StatQuery query) {
        LambdaQueryWrapper<StatPartsProgressDO> queryWrapper = Wrappers.lambdaQuery(StatPartsProgressDO.class)
                .ne(StatPartsProgressDO::getFinishedRate, "100%")
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatPartsProgressDO::getProjectCode, query.getProjectCode());
        List<StatPartsProgressDO> list = statPartsProgressMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatPartProgressList(list);
    }

    /**
     * 查询零件进度
     */
    public List<StatPartsProgress> selectList(StatQuery query) {
        LambdaQueryWrapper<StatPartsProgressDO> queryWrapper = Wrappers.lambdaQuery(StatPartsProgressDO.class)
                .eq(StrUtil.isNotEmpty(query.getProjectCode()), StatPartsProgressDO::getProjectCode, query.getProjectCode());
        List<StatPartsProgressDO> list = statPartsProgressMapper.selectList(queryWrapper);
        return statisticsTransfer.toStatPartProgressList(list);
    }
}
