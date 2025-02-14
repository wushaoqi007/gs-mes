package com.greenstone.mes.market.application.assembler;

import com.greenstone.mes.market.application.dto.MarketAppSaveCmd;
import com.greenstone.mes.market.application.dto.result.MarketAppResult;
import com.greenstone.mes.market.domain.entity.MarketApplication;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-07-14:27
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MarketAppAssembler {
    // MarketApplication
    MarketAppResult toMarketAppResult(MarketApplication application);

    List<MarketAppResult> toMarketAppResults(List<MarketApplication> applications);

    MarketApplication fromMarketAppSaveCmd(MarketAppSaveCmd saveCmd);

}
