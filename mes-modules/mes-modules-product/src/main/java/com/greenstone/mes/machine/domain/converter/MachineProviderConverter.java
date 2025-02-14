package com.greenstone.mes.machine.domain.converter;

import com.greenstone.mes.machine.domain.entity.MachineProvider;
import com.greenstone.mes.machine.infrastructure.persistence.MachineProviderDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MachineProviderConverter {

    MachineProviderDO entity2Do(MachineProvider machineProvider);

    List<MachineProviderDO> entities2Dos(List<MachineProvider> machineProviders);


    MachineProvider do2Entity(MachineProviderDO machineProviderDO);

    List<MachineProvider> dos2Entities(List<MachineProviderDO> machineProviderDOS);


}
