package com.greenstone.mes.market.domain.converter;

import com.greenstone.mes.market.domain.entity.MarketApplication;
import com.greenstone.mes.market.domain.entity.MarketApplicationAttachment;
import com.greenstone.mes.market.infrastructure.persistence.MarketAppAttachmentDo;
import com.greenstone.mes.market.infrastructure.persistence.MarketAppDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-11-07-14:29
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MarketAppConverter {
    // MarketApplication
    MarketAppDo toMarketApplicationDo(MarketApplication marketApplication);

    default String listToString(List<Long> list) {
        return cn.hutool.core.collection.CollUtil.join(list, ",");
    }

    @Mapping(target = "submitBy", source = "appliedByName")
    MarketApplication toMarketApplication(MarketAppDo marketAppDo);

    default List<Long> stringToList(String s) {
        return cn.hutool.core.util.StrUtil.split(s, ',', -1, true, Long::valueOf);
    }

    MarketAppAttachmentDo toMarketApplicationAttachmentDo(MarketApplicationAttachment attachment);

    MarketApplicationAttachment toMarketApplicationAttachment(MarketAppAttachmentDo attachmentDo);

    List<MarketAppAttachmentDo> toMarketApplicationAttachmentDos(List<MarketApplicationAttachment> attachments);

    List<MarketApplicationAttachment> toMarketApplicationAttachments(List<MarketAppAttachmentDo> attachmentDos);

    default MarketApplication toMarketApplication(MarketAppDo marketAppDo, List<MarketAppAttachmentDo> attachmentDos) {
        MarketApplication marketApplication = toMarketApplication(marketAppDo);
        marketApplication.setAttachments(toMarketApplicationAttachments(attachmentDos));
        return marketApplication;
    }
}
