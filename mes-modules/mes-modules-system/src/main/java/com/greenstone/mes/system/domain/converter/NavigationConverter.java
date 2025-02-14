package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.domain.entity.Navigation;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.infrastructure.po.NavigationDO;
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
public interface NavigationConverter {

    NavigationDO entity2Do(Navigation navigation);

    List<NavigationDO> entities2Dos(List<Navigation> navigations);

    Navigation do2Entity(NavigationDO navigationDO);

    List<Navigation> dos2Entities(List<NavigationDO> navigationDOS);

    List<MemberNavigationResult> dos2Results(List<NavigationDO> navigationDOS);

    @Mapping(target = "navigationId", source = "id")
    @Mapping(target = "navigationName", source = "name")
    MemberNavigationResult dos2Result(NavigationDO navigationDOS);
}
