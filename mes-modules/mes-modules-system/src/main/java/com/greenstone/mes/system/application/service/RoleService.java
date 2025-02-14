package com.greenstone.mes.system.application.service;

import com.greenstone.mes.system.application.dto.cmd.RoleAllocateUsersCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleUserChangeCmd;
import com.greenstone.mes.system.application.dto.cmd.RoleUserRemoveCmd;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.entity.Role;

import java.util.List;

public interface RoleService {
    void saveRole(Role role);

    void updateRole(Role role);

    void removeRole(Long roleId, Boolean keepMemberPerm);

    List<Role> list(Role role);

    Role detail(Long roleId);

    void allocateUsers(RoleAllocateUsersCmd allocateUsersCmd);

    void addRoleUser(RoleUserChangeCmd changeCmd);

    void changeRoleUser(RoleUserChangeCmd changeCmd);

    void updateRoleUser(RoleUserChangeCmd changeCmd);

    void removeRoleUser(RoleUserRemoveCmd removeCmd);

    List<User> unallocatedUsers(User user);

    List<User> allocatedUsers(Long roleId);

    List<Role> selectUnallocatedPermRoles(Long functionPermissionId);
}
