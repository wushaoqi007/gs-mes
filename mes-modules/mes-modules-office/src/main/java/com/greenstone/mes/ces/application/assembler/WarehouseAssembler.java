package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.WarehouseAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseEditCmd;
import com.greenstone.mes.ces.application.dto.result.WarehouseResult;
import com.greenstone.mes.ces.domain.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-01-10:53
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseAssembler {

    //Warehouse
    WarehouseResult toWarehouseResult(Warehouse itemSpec);

    List<WarehouseResult> toWarehouseResultS(List<Warehouse> itemSpecList);

    Warehouse toWarehouse(WarehouseAddCmd addCmd);

    Warehouse toWarehouse(WarehouseEditCmd editCmd);

}
