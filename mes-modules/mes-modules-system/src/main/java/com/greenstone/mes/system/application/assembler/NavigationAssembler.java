package com.greenstone.mes.system.application.assembler;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.system.application.dto.cmd.NavigationAddCmd;
import com.greenstone.mes.system.application.dto.result.NavigationResult;
import com.greenstone.mes.system.application.dto.result.NavigationSelectResult;
import com.greenstone.mes.system.application.dto.result.NavigationTree;
import com.greenstone.mes.system.domain.entity.Navigation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class, List.class, StrUtil.class}
)
public interface NavigationAssembler {


    List<NavigationResult> toNavigationRs(List<Navigation> navigations);

    NavigationResult toNavigationR(Navigation navigation);

    Navigation toNavigation(NavigationAddCmd cmd);

    List<NavigationTree> toNavigationTree(List<NavigationResult> results);

    List<NavigationSelectResult> toNavigationSelectRs(List<Navigation> navigations);

    @Mapping(target = "navigationId", source = "id")
    @Mapping(target = "navigationName", source = "name")
    NavigationSelectResult toNavigationSelectR(Navigation navigations);
}
