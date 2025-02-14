package com.greenstone.mes.material.domain.converter;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.material.infrastructure.enums.BillOperation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {StrUtil.class}
)
public interface EnumConverter {

    default BillOperation toStockOperation(int id) {
        return BillOperation.getById(id);
    }

    default int toStockOperationId(BillOperation operation) {
        return operation.getId();
    }

}
