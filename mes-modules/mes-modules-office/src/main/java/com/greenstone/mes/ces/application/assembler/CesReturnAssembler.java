package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.CesReturnAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseInAddCmd;
import com.greenstone.mes.ces.application.dto.event.CesReturnAddE;
import com.greenstone.mes.ces.application.dto.result.CesReturnResult;
import com.greenstone.mes.ces.domain.entity.CesReturn;
import com.greenstone.mes.ces.domain.entity.CesReturnItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CesReturnAssembler {
    CesReturn toCesReturn(CesReturnAddCmd addCmd);

    CesReturnResult toCesReturnR(CesReturn cesReturn);

    List<CesReturnResult> toCesReturnRs(List<CesReturn> cesReturns);

    @Mapping(target = "inStockNum", source = "returnNum")
    @Mapping(target = "returnSerialNo", source = "serialNo")
    WarehouseInAddCmd.Item toWarehouseInAddCmdItem(CesReturnItem cesReturnItem);

}
