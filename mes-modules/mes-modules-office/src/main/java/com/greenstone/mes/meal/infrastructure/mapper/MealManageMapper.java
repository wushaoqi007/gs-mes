package com.greenstone.mes.meal.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.meal.infrastructure.persistence.MealManageDo;
import org.springframework.stereotype.Repository;

@Repository
public interface MealManageMapper extends EasyBaseMapper<MealManageDo> {
}
