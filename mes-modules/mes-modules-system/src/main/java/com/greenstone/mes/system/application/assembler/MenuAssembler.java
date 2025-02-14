package com.greenstone.mes.system.application.assembler;

import com.greenstone.mes.system.domain.SysMenu;
import com.greenstone.mes.system.dto.cmd.MenuAddCmd;
import com.greenstone.mes.system.dto.cmd.MenuEditCmd;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MenuAssembler {

    SysMenu addCmd2Entity(MenuAddCmd menuAddCmd);

    SysMenu editCmd2Entity(MenuEditCmd menuEditCmd);
}
