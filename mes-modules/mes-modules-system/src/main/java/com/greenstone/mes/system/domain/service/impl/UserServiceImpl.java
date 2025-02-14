package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.application.assembler.UserAssembler;
import com.greenstone.mes.system.application.dto.cmd.RoleUserChangeCmd;
import com.greenstone.mes.system.application.service.RoleService;
import com.greenstone.mes.system.domain.Permission;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.entity.MemberNavigation;
import com.greenstone.mes.system.domain.entity.MemberPermission;
import com.greenstone.mes.system.domain.entity.RoleUser;
import com.greenstone.mes.system.domain.repository.*;
import com.greenstone.mes.system.domain.service.ISysDeptService;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.dto.auth.UserLoginAuth;
import com.greenstone.mes.system.dto.cmd.UserWxSyncCmd;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.dto.result.UserResult;
import com.greenstone.mes.system.infrastructure.constant.UserConst;
import com.greenstone.mes.system.infrastructure.enums.MemberType;
import com.greenstone.mes.system.infrastructure.mapper.SysUserOldMapper;
import com.greenstone.mes.system.infrastructure.mapper.UserMapper;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import com.greenstone.mes.system.infrastructure.po.MemberPermissionDO;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import com.greenstone.mes.system.interfaces.rest.cmd.UserAddCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserEditCmd;
import com.greenstone.mes.system.interfaces.rest.cmd.UserResetPassword;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.helper.impl.WxcpHelper;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final SysUserOldMapper sysUserOldMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserAssembler userAssembler;
    private final WxUserService wxUserService;
    private final ISysDeptService deptService;
    private final WxCpProperties wxCpProperties;
    private final MemberPermissionRepository memberPermissionRepository;
    private final MemberNavigationRepository memberNavigationRepository;
    private final NavigationRepository navigationRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final MsgProducer msgProducer;

    @Override
    public UserLoginAuth getUserByLoginUsername(String loginUsername) {
        // 账号直接登录
        UserPo userPo = userMapper.getOneOnly(UserPo.builder().userName(loginUsername).deleted(false).build());
        if (userPo == null) {
            // 手机号登录
            if (loginUsername.length() == 11 && StrUtil.isNumeric(loginUsername)) {
                userPo = userMapper.getOneOnly(UserPo.builder().phonenumber(loginUsername).deleted(false).build());
            }
        }
        if (userPo == null) {
            // 工号登录
            if (loginUsername.length() == 6 && loginUsername.startsWith("G")) {
                userPo = userMapper.getOneOnly(UserPo.builder().employeeNo(loginUsername).deleted(false).build());
            }
        }
        if (userPo != null) {
            UserLoginAuth userLoginAuth = new UserLoginAuth();
            userLoginAuth.setUserId(userPo.getUserId());
            userLoginAuth.setUserName(userPo.getUserName());
            userLoginAuth.setPassword(userPo.getPassword());
            userLoginAuth.setEmployeeNo(userPo.getEmployeeNo());
            userLoginAuth.setNickName(userPo.getNickName());
            userLoginAuth.setDeleted(userPo.getDeleted());
            return userLoginAuth;
        }

        return null;
    }

    @Transactional
    @Override
    public void createUser(UserAddCmd userAddCmd) {
        if (userMapper.selectCount(UserPo.builder().userName(userAddCmd.getUserName()).build()) > 0) {
            log.error("用户名已存在");
            throw new RuntimeException("用户名已存在");
        }
        if (StrUtil.isNotEmpty(userAddCmd.getPhonenumber()) &&
                userMapper.selectCount(UserPo.builder().phonenumber(userAddCmd.getPhonenumber()).build()) > 0) {
            log.error("手机号已存在");
            throw new RuntimeException("手机号已存在");
        }

        if (userAddCmd.getDeptId() != null && deptService.selectDeptById(userAddCmd.getDeptId()) == null) {
            log.error("部门不存在");
            throw new RuntimeException("部门不存在，请重新选择");
        }

        if (userAddCmd.getRoleId() != null && roleRepository.selectRoleById(userAddCmd.getRoleId()) == null) {
            log.error("角色不存在");
            throw new RuntimeException("角色不存在，请重新选择");
        }

        if (userAddCmd.getRoleId() != null && MemberType.USER.getType().equals(userAddCmd.getPermissionType())) {
            log.error("个人权限不能分配角色");
            throw new RuntimeException("个人权限不能分配角色");
        }

        UserPo userPo = fromUserAddCmd(userAddCmd);
        userMapper.insert(userPo);

        //  新增角色关系
        roleService.addRoleUser(RoleUserChangeCmd.builder().permissionType(userAddCmd.getPermissionType()).roleId(userAddCmd.getRoleId()).userId(userPo.getUserId()).build());
    }

    @Transactional
    @Override
    public void updateUser(UserEditCmd userEditCmd) {
        if (StrUtil.isNotEmpty(userEditCmd.getPhonenumber())) {
            LambdaQueryWrapper<UserPo> queryWrapper = Wrappers.lambdaQuery(UserPo.class).ne(UserPo::getUserId, userEditCmd.getUserId()).eq(UserPo::getPhonenumber, userEditCmd.getPhonenumber());
            if (userMapper.selectCount(queryWrapper) > 0) {
                log.error("手机号已存在");
                throw new RuntimeException("手机号已存在");
            }
        }

        if (userEditCmd.getDeptId() != null && deptService.selectDeptById(userEditCmd.getDeptId()) == null) {
            log.error("部门不存在");
            throw new RuntimeException("部门不存在，请重新选择");
        }

        if (userEditCmd.getRoleId() != null && roleRepository.selectRoleById(userEditCmd.getRoleId()) == null) {
            log.error("角色不存在");
            throw new RuntimeException("角色不存在，请重新选择");
        }

        if (userEditCmd.getRoleId() != null && MemberType.USER.getType().equals(userEditCmd.getPermissionType())) {
            log.error("个人权限不能分配角色");
            throw new RuntimeException("个人权限不能分配角色");
        }

        UserPo userPo = fromUserEditCmd(userEditCmd);
        userMapper.updateById(userPo);

        //  修改角色关系
        roleService.updateRoleUser(RoleUserChangeCmd.builder().permissionType(userEditCmd.getPermissionType()).roleId(userEditCmd.getRoleId()).userId(userPo.getUserId()).build());
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.getById(userId);
    }

    @Override
    public User getUserByMail(String mail) {
        return userMapper.getByMail(mail);
    }

    @Override
    public List<User> getUsers(User user) {
        return userMapper.getUsers(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userMapper.selectCount(UserPo.builder().userId(userId).build()) == 0) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(userId);
        // 删除角色关系
        roleService.updateRoleUser(RoleUserChangeCmd.builder().permissionType(MemberType.ROLE.getType()).roleId(null).userId(userId).build());
    }

    @Override
    public List<User> getByWxCp() {
        return userMapper.getByWxCp(wxCpProperties.getDefaultCpId());
    }

    @Override
    public void resetPassword(UserResetPassword resetPassword) {
        userMapper.updateById(UserPo.builder().userId(resetPassword.getUserId()).password(resetPassword.getPassword()).build());
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public User getUserByWx(String wxCpId, String wxUserId) {
        return userMapper.getByWx(wxCpId, wxUserId);
    }

    @Override
    public List<SysUser> list() {
        LambdaQueryWrapper<SysUser> select = Wrappers.lambdaQuery(SysUser.class).select(SysUser::getUserId, SysUser::getNickName);
        return sysUserOldMapper.selectList(select);
    }

    @Override
    public List<UserResult> queryUserBriefInfos(UserQuery query) {
        List<UserPo> userDos = userRepository.queryBriefInfos(query);
        return userAssembler.dos2Results(userDos);
    }

    @Override
    public List<UserResult> queryUserWorkwxInfos(UserQuery query) {
        List<UserPo> userDos = userRepository.queryWorkwxInfos(query);
        return userAssembler.dos2Results(userDos);
    }

    @Override
    public void syncWorkwxUser(UserWxSyncCmd syncCmd) {
        List<Long> userIds = syncCmd.getUserIds();
        for (Long userId : userIds) {
            UserPo userDo = userRepository.getById(userId);
            WxCpUser wxUser;
            try {
                wxUser = wxUserService.getUser(new CpId(userDo.getWxCpId()), new WxUserId(userDo.getWxUserId()));
            } catch (Exception e) {
                log.error("获取企业微信用户失败：" + userDo.getNickName() + " " + userDo.getWxCpId() + " " + userDo.getWxUserId());
                continue;
            }
            DeptPo sysDept = deptService.getSysDept(DeptPo.builder().cpId(userDo.getWxCpId()).wxDeptId(wxUser.getDepartIds()[0]).build());

            UserPo updateUser = UserPo.builder().userId(userDo.getUserId())
                    .deptId(sysDept.getDeptId())
                    .position(wxUser.getPosition())
                    .nickName(wxUser.getName()).build();

            wxUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> updateUser.setEmployeeNo(a.getTextValue()));
            userRepository.updateById(updateUser);
        }
    }

    @Override
    public List<SysUser> getUsersWithoutEmpNo() {
        LambdaQueryWrapper<SysUser> queryWrapper =
                Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getMainWxcpId, "wx1dee7aa3b2526c66").eq(SysUser::getDeleted, "0").isNull(SysUser::getEmployeeNo);
        return sysUserOldMapper.selectList(queryWrapper);
    }

    @Override
    public List<User> selectUnallocatedPermUsers(Long functionPermissionId) {
        // 已分配该权限组的用户
        List<MemberPermissionDO> memberPermissions = memberPermissionRepository.selectMemberPermissionsByFunctionPermId(functionPermissionId);
        Set<Long> userSet = memberPermissions.stream().filter(m -> m.getMemberType().equals(MemberType.USER.getType())).map(MemberPermissionDO::getMemberId).collect(Collectors.toSet());
        // 未分配角色的用户
        List<User> users = roleRepository.selectUnallocatedUsers(User.builder().build());
        // 未分配该权限组的用户=未分配角色的用户-已分配该权限组的用户
        return users.stream().filter(user -> !userSet.contains(user.getUserId())).toList();
    }

    @Override
    public List<UserPermissionResult> selectUserPermissions(Long userId) {
        List<UserPermissionResult> userPermissionResults = new ArrayList<>();
        // 成员权限
        List<MemberPermission> memberPermissions;
        // 成员有哪些导航权限
        List<MemberNavigation> memberNavigations;
        RoleUser roleUser = roleRepository.selectRoleUserByUserId(userId);
        if (roleUser != null) {
            // 角色权限
            memberPermissions = memberPermissionRepository.selectDetailsByMemberId(roleUser.getRoleId());
            memberNavigations = memberNavigationRepository.selectDetailsByMemberId(roleUser.getRoleId());
        } else {
            // 用户权限
            memberPermissions = memberPermissionRepository.selectDetailsByMemberId(userId);
            memberNavigations = memberNavigationRepository.selectDetailsByMemberId(userId);
        }
        if (CollUtil.isNotEmpty(memberPermissions) && CollUtil.isNotEmpty(memberNavigations)) {
            for (MemberPermission memberPermission : memberPermissions) {
                UserPermissionResult userPerm = UserPermissionResult.builder().functionPermissionId(memberPermission.getFunctionPermissionId())
                        .functionId(memberPermission.getFunctionId())
                        .functionName(memberPermission.getFunctionName())
                        .functionType(memberPermission.getFunctionType())
                        .permissionGroupName(memberPermission.getPermissionGroupName())
                        .permissionGroupTypeName(memberPermission.getPermissionGroupTypeName())
                        .rights(memberPermission.getRights())
                        .viewFilter(memberPermission.getViewFilter())
                        .updateFilter(memberPermission.getUpdateFilter())
                        .source(memberPermission.getSource())
                        .component(memberPermission.getComponent())
                        .formComponent(memberPermission.getFormComponent())
                        .usingProcess(memberPermission.getUsingProcess())
                        .templateId(memberPermission.getTemplateId())
                        .orderNum(memberPermission.getOrderNum()).build();
                userPerm.setPermission(toPermission(memberPermission));
                userPermissionResults.add(userPerm);
                // 功能权限存在则导航存在
                List<MemberNavigation> memberNavigationsOfThisFunction = memberNavigations.stream().filter(mn -> Objects.equals(memberPermission.getFunctionId(), mn.getFunctionId())).toList();
                List<UserPermissionResult.Navigation> navigationsOfThisFunction = memberNavigationsOfThisFunction.stream().map(m -> UserPermissionResult.Navigation.builder().navigationId(m.getNavigationId()).navigationName(m.getNavigationName()).build()).toList();
                userPerm.setNavigations(navigationsOfThisFunction);
            }
        }

        return userPermissionResults;
    }

    private Permission toPermission(MemberPermission memberPermission) {
        return Permission.builder()
                .rights(memberPermission.getRights())
                .updateFilter(memberPermission.getUpdateFilter())
                .viewFilter(memberPermission.getViewFilter()).build();
    }

    @Override
    public List<MemberNavigationResult> selectUserNavigations(Long userId) {
        List<MemberNavigationResult> memberNavigationResults;
        // 管理员返回所有导航
        if (isAdmin(userId)) {
            return navigationRepository.listAllOfAdmin();
        }
        RoleUser roleUser = roleRepository.selectRoleUserByUserId(userId);
        if (roleUser != null) {
            memberNavigationResults = memberNavigationRepository.selectMemberNavigation(roleUser.getRoleId());
        } else {
            memberNavigationResults = memberNavigationRepository.selectMemberNavigation(userId);
        }
        return memberNavigationResults;
    }

    @Override
    public List<MemberFunctionResult> selectUserFunctions(Long userId) {
        List<MemberFunctionResult> memberFunctionResultList;
        RoleUser roleUser = roleRepository.selectRoleUserByUserId(userId);
        if (roleUser != null) {
            memberFunctionResultList = memberPermissionRepository.selectMemberFunctions(roleUser.getRoleId());
        } else {
            memberFunctionResultList = memberPermissionRepository.selectMemberFunctions(userId);
        }
        return memberFunctionResultList;
    }

    @Override
    public void updateEmail(User user) {
        UserPo userUpdate = UserPo.builder().userId(user.getUserId()).email(user.getEmail()).build();
        userMapper.updateById(userUpdate);
    }

    @Override
    public void syncWxEmployeeNo() {
        log.info("用户工号同步，开始同步用户工号");
        List<User> noEmployeeNoUsers = userMapper.getUserHaveNoEmployeeNo();
        if (CollUtil.isEmpty(noEmployeeNoUsers)) {
            log.info("用户工号同步，没有需要同步工号的用户");
            return;
        }

        for (User user : noEmployeeNoUsers) {
            if (StrUtil.isEmpty(user.getWxCpId()) || StrUtil.isEmpty(user.getWxUserId())) {
                log.error("用户工号同步，用户 {} 无法同步工号：企业微信信息缺失", user.getNickName());
                continue;
            }
            WxCpUser wxCpUser;
            try {
                wxUserService.refreshUser();
                // TODO 通过方法名显式区分是否只用缓存
                wxCpUser = wxUserService.getUser(new CpId(user.getWxCpId()), new WxUserId(user.getWxUserId()));
            } catch (Exception e) {
                log.error("用户工号同步，获取企业微信用户失败，用户id： {}", user.getWxUserId());
                continue;
            }
            if (wxCpUser == null) {
                log.error("用户工号同步，用户 {} 在企业微信中不存在", user.getWxUserId());
                continue;
            }
            String employeeNo = WxcpHelper.getEmployeeNo(wxCpUser);
            if (StrUtil.isEmpty(employeeNo)) {
                log.warn("用户工号同步，无法给用户 {} 设置工号，还未在企业微信设置工号", user.getNickName());
            } else {
                UserPo userUpdate = UserPo.builder().userId(user.getUserId()).employeeNo(employeeNo).build();
                userMapper.updateById(userUpdate);
                log.info("用户工号同步，成功同步用户：{}，工号：{}", user.getNickName(), employeeNo);

                User user2 = getUserById(user.getUserId());
                try {
                    msgProducer.send(MqConst.Topic.USER_EMPLOYNO_ADDED, user2);
                } catch (ExecutionException | InterruptedException e) {
                    log.error("发送MQ消息失败", e);
                }
            }
        }
        log.info("用户工号同步，工号同步完成");
    }

    public Boolean isAdmin(Long userId) {
        LambdaQueryWrapper<UserPo> queryWrapper = Wrappers.lambdaQuery(UserPo.class)
                .eq(UserPo::getUserId, userId).eq(UserPo::getUserType, "admin");
        return userMapper.selectCount(queryWrapper) > 0;
    }

    private UserPo fromUserAddCmd(UserAddCmd userAddCmd) {
        return UserPo.builder().userName(userAddCmd.getUserName())
                .nickName(userAddCmd.getNickName())
                .employeeNo(userAddCmd.getEmployeeNo())
                .email(userAddCmd.getEmail())
                .phonenumber(userAddCmd.getPhonenumber())
                .avatar(userAddCmd.getAvatar())
                .position(userAddCmd.getPosition())
                .roleId(userAddCmd.getRoleId())
                .deptId(userAddCmd.getDeptId())
                .wxUserId(userAddCmd.getWxUserId())
                .wxCpId(userAddCmd.getWxCpId())
                .sex(userAddCmd.getSex())
                .password(userAddCmd.getPassword())
                .userType(UserConst.UserType.USER).build();
    }

    private UserPo fromUserEditCmd(UserEditCmd userEditCmd) {
        return UserPo.builder().userId(userEditCmd.getUserId())
                .nickName(userEditCmd.getNickName())
                .employeeNo(userEditCmd.getEmployeeNo())
                .email(userEditCmd.getEmail())
                .phonenumber(userEditCmd.getPhonenumber())
                .avatar(userEditCmd.getAvatar())
                .position(userEditCmd.getPosition())
                .roleId(userEditCmd.getRoleId())
                .deptId(userEditCmd.getDeptId())
                .wxUserId(userEditCmd.getWxUserId())
                .wxCpId(userEditCmd.getWxCpId())
                .sex(userEditCmd.getSex()).build();
    }
}
