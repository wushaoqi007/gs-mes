package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.auth.UserLoginAuth;
import com.greenstone.mes.system.dto.cmd.UserWxSyncCmd;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.dto.result.UserResult;
import com.greenstone.mes.system.interfaces.rest.cmd.UserAddCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserEditCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserResetPassword;

import java.util.List;

public interface UserService {

    UserLoginAuth getUserByLoginUsername(String loginUsername);

    void createUser(UserAddCmd userAddCmd);

    void updateUser(UserEditCmd userEditCmd);

    User getUserById(Long userId);

    User getUserByMail(String mail);

    User getUserByUsername(String username);

    User getUserByWx(String wxCpId, String wxUserId);

    List<User> getUsers(User user);

    void deleteUser(Long userId);

    List<User> getByWxCp();

    void resetPassword(UserResetPassword resetPassword);

    List<SysUser> list();

    List<UserResult> queryUserBriefInfos(UserQuery query);

    List<UserResult> queryUserWorkwxInfos(UserQuery query);

    void syncWorkwxUser(UserWxSyncCmd syncCmd);

    List<SysUser> getUsersWithoutEmpNo();

    List<User> selectUnallocatedPermUsers(Long functionPermissionId);

    List<UserPermissionResult> selectUserPermissions(Long userId);

    List<MemberNavigationResult> selectUserNavigations(Long userId);

    List<MemberFunctionResult> selectUserFunctions(Long userId);

    void updateEmail(User user);

    void syncWxEmployeeNo();
}
