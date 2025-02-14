package com.greenstone.mes.product.domain.converter;

import com.greenstone.mes.product.domain.entity.ProductPlan;
import com.greenstone.mes.product.infrastructure.persistence.ProductPlanDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductPlanConverter {

    ProductPlan toProductPlan(ProductPlanDO planDO);

    List<ProductPlan> toProductPlans(List<ProductPlanDO> planDOS);

    ProductPlanDO toProductPlanDO(ProductPlan productPlan);

    List<ProductPlanDO> toProductPlanDOs(List<ProductPlan> productPlans);

}
