package com.greenstone.mes.product.application.service;

import com.greenstone.mes.product.application.dto.cmd.ProductPlanStatusChangeCmd;
import com.greenstone.mes.product.domain.entity.ProductPlan;
import com.greenstone.mes.product.infrastructure.mapper.ProductPlanMapper;
import com.greenstone.mes.product.infrastructure.persistence.ProductPlanDO;
import com.greenstone.mes.table.core.TableService;

public interface ProductPlanService extends TableService<ProductPlan, ProductPlanDO, ProductPlanMapper> {

    void statusChange(ProductPlanStatusChangeCmd statusChangeCmd);

}
