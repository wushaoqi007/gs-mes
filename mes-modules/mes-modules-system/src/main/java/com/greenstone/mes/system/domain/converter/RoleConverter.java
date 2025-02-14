package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.domain.entity.Role;
import com.greenstone.mes.system.domain.entity.RoleUser;
import com.greenstone.mes.system.infrastructure.po.RoleDO;
import com.greenstone.mes.system.infrastructure.po.RoleUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleConverter {

    RoleDO entity2Do(Role function);

    List<RoleDO> entities2Dos(List<Role> functions);

    Role do2Entity(RoleDO functionDO);

    List<Role> dos2Entities(List<RoleDO> functionDOS);

    // roleUser
    List<RoleUser> roleUserDos2Entities(List<RoleUserDO> roleUserDOS);

    RoleUser roleUserDo2Entity(RoleUserDO roleUserDO);

    List<RoleUserDO> roleUserEntities2Dos(List<RoleUser> allocatedRoleUsers);

    RoleUserDO roleUserEntity2Do(RoleUser allocatedRoleUser);
}
