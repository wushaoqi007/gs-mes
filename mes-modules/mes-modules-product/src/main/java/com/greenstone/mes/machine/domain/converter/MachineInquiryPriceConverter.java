package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineInquiryPrice;
import com.greenstone.mes.machine.domain.entity.MachineInquiryPriceDetail;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineInquiryPriceConverter {

    // MachineInquiryPrice
    MachineInquiryPriceDO entity2Do(MachineInquiryPrice machineInquiryPrice);

    List<MachineInquiryPriceDO> entities2Dos(List<MachineInquiryPrice> machineInquiryPrices);

    MachineInquiryPrice do2Entity(MachineInquiryPriceDO machineInquiryPriceDO);

    List<MachineInquiryPrice> dos2Entities(List<MachineInquiryPriceDO> machineInquiryPriceDOS);

    // MachineInquiryPriceDetail
    MachineInquiryPriceDetailDO detailEntity2Do(MachineInquiryPriceDetail machineInquiryPriceDetail);

    List<MachineInquiryPriceDetailDO> detailEntities2Dos(List<MachineInquiryPriceDetail> machineInquiryPriceDetails);

    MachineInquiryPriceDetail detailDo2Entity(MachineInquiryPriceDetailDO machineInquiryPriceDetailDO);

    List<MachineInquiryPriceDetail> detailDos2Entities(List<MachineInquiryPriceDetailDO> machineInquiryPriceDetailDOS);

}
