package com.greenstone.mes.material.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.material.application.dto.StatDailyQuery;
import com.greenstone.mes.material.domain.entity.StatDataDesigner;
import com.greenstone.mes.material.infrastructure.persistence.StatResultDesignerDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Repository
public interface StatResultDesignerMapper extends EasyBaseMapper<StatResultDesignerDO> {
    List<StatDataDesigner> statDataForDesigner(StatDailyQuery statDailyQuery);
}
