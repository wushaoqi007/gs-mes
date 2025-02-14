package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.domain.entity.StatDataParts;
import com.greenstone.mes.material.infrastructure.persistence.StatPartsProgressDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Repository
public interface StatPartsProgressMapper extends EasyBaseMapper<StatPartsProgressDO> {
    List<StatDataParts> statDataForOngoingParts(StatDailyQuery statDailyQuery);
}
