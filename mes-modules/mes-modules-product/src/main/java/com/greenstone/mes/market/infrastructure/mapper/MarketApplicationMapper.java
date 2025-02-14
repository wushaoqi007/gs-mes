package com.greenstone.mes.market.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.market.infrastructure.persistence.MarketAppDo;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketApplicationMapper extends EasyBaseMapper<MarketAppDo> {
}
