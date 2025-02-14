package com.greenstone.mes.product.infrastructure.mapper;

import com.greenstone.mes.product.infrastructure.persistence.ProductPlanDO;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPlanMapper extends TableBaseMapper<ProductPlanDO> {
}
