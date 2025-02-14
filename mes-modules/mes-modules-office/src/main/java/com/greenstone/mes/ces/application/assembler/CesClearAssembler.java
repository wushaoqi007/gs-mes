package com.greenstone.mes.ces.application.assembler;

import com.greenstone.mes.ces.application.dto.cmd.CesClearAddCmd;
import com.greenstone.mes.ces.application.dto.cmd.CesClearEditCmd;
import com.greenstone.mes.ces.application.dto.cmd.WarehouseOutAddCmd;
import com.greenstone.mes.ces.application.dto.result.CesClearResult;
import com.greenstone.mes.ces.domain.entity.CesClear;
import com.greenstone.mes.ces.domain.entity.CesClearItem;
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
public interface CesClearAssembler {
    CesClear toCesClear(CesClearAddCmd addCmd);

    CesClear toCesClear(CesClearEditCmd addCmd);

    CesClearResult toCesClearR(CesClear cesClear);

    List<CesClearResult> toCesClearRs(List<CesClear> cesClears);

    @Mapping(target = "outStockNum", source = "clearNum")
    @Mapping(target = "clearSerialNo", source = "serialNo")
    WarehouseOutAddCmd.Item toWarehouseOutAddCmdItem(CesClearItem cesClearItem);

}
