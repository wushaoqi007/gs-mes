package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.domain.entity.StatDataCheckRecord;
import com.greenstone.mes.material.domain.entity.StatDataDaily;
import com.greenstone.mes.material.domain.entity.StatPartsDataSource;
import com.greenstone.mes.material.infrastructure.persistence.StatResultDailyDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Repository
public interface StatResultDailyMapper extends EasyBaseMapper<StatResultDailyDO> {

    List<StatDataDaily> statDataFromPartStage(StatDailyQuery statDailyQuery);

    List<StatDataDaily> statDataFromStockRecord(StatDailyQuery statDailyQuery);

    List<StatDataCheckRecord> statDataFromCheckRecord(StatDailyQuery statDailyQuery);

    List<StatPartsDataSource> selectDailyOweSource(StatDailyQuery statDailyQuery);

    List<StatPartsDataSource> selectDailyFinishSource(StatDailyQuery statDailyQuery);

    List<StatPartsDataSource> selectDailyDeliverySource(StatDailyQuery statDailyQuery);

    List<StatDataDaily> selectStatDataDaily(StatDailyQuery statDailyQuery);
}
