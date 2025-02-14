package com.greenstone.mes.system.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.common.core.constant.UserConstants;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.common.core.utils.bean.BeanValidators;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.mq.consts.MqConst;
import com.greenstone.mes.mq.producer.MsgProducer;
import com.greenstone.mes.system.api.domain.SysRole;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.SysPost;
import com.greenstone.mes.system.domain.SysUserPost;
import com.greenstone.mes.system.domain.SysUserRole;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.domain.service.ISysConfigService;
import com.greenstone.mes.system.domain.service.ISysDeptService;
import com.greenstone.mes.system.domain.service.SysUserService;
import com.greenstone.mes.system.domain.service.UserService;
import com.greenstone.mes.system.infrastructure.mapper.*;
import com.greenstone.mes.system.infrastructure.po.DeptPo;
import com.greenstone.mes.system.infrastructure.po.UserPo;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpOAuth2Service;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.WxCpUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserOldMapper, SysUser> implements SysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserOldMapper oldUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    protected Validator validator;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private MsgProducer<User> msgProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private WxcpService wxcpService;

    @Autowired
    private WxCpProperties wxCpProperties;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user) {
        return oldUserMapper.selectUserList(user);
    }

    @Override
    public List<SysUser> allUsers(SysUser user) {
        return oldUserMapper.list(user);
    }


    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return oldUserMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user) {
        return oldUserMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return oldUserMapper.getOneOnly(SysUser.builder().userName(userName).build());
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return oldUserMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName) {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName) {
        int count = oldUserMapper.checkUserNameUnique(userName);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = oldUserMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = oldUserMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users)) {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int createUser(SysUser user) {
        if (user.getDeptId() != null && (user.getRoleIds() == null || user.getRoleIds().length == 0)) {
            DeptPo deptDo = deptService.selectDeptById(user.getDeptId());
            if (StrUtil.isNotEmpty(deptDo.getDefaultRoleId())) {
                Long[] roleIds = StrUtil.split(deptDo.getDefaultRoleId(), ",").stream().map(Long::valueOf).toList().toArray(new Long[]{});
                user.setRoleIds(roleIds);
            }
        }

        // 新增用户信息
        int rows = oldUserMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);

        // 新增部门成员默认的角色
        if (user.getDeptId() != null && (user.getRoleIds() == null || user.getRoleIds().length == 0)) {
            DeptPo dept = deptService.selectDeptById(user.getDeptId());
            if (StrUtil.isNotEmpty(dept.getDefaultRoleId())) {
                String[] roleIdStrs = dept.getDefaultRoleId().split(",");
                Long[] roleIds = new Long[roleIdStrs.length];
                for (int i = 0; i < roleIdStrs.length; i++) {
                    roleIds[i] = Long.valueOf(roleIdStrs[i]);
                }
                user.setRoleIds(roleIds);
            }
        }

        // 新增用户与角色管理
        insertUserRole(user);

        // 发布用户创建消息
        try {
            User user1 = userMapper.getById(user.getUserId());
            msgProducer.send(MqConst.Topic.USER_CREATE, user1);
        } catch (ExecutionException | InterruptedException e) {
            log.error("发送mq消息失败", e);
        }

        return rows;
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user) {
        return oldUserMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色关联
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位关联
        insertUserPost(user);

        return oldUserMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return oldUserMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return oldUserMapper.updateUser(user);
    }

    @Override
    public void updateUserProfile(User user) {
        UserPo userPo = UserPo.builder().employeeNo(user.getEmployeeNo())
                .email(user.getEmail())
                .userName(user.getUserName())
                .wxUserId(user.getWxUserId())
                .deptId(user.getDeptId())
                .nickName(user.getNickName())
                .phonenumber(user.getPhonenumber())
                .wxCpId(user.getWxCpId())
                .userType(user.getUserType())
                .avatar(user.getAvatar())
                .build();
        userMapper.updateById(userPo);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar   头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar) {
        return oldUserMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user) {
        return oldUserMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password) {
        return oldUserMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts) {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0) {
                userPostMapper.batchUserPost(list);
            }
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return oldUserMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return oldUserMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList) {
            try {
                // 验证是否存在这个用户
                SysUser u = oldUserMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u)) {
                    BeanValidators.validateWithException(validator, user);
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.createUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                } else if (isUpdateSupport) {
                    BeanValidators.validateWithException(validator, user);
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public SysUser getNickNameByUserName(String userName) {
        return oldUserMapper.getNickNameByUserName(userName);
    }

    public List<Long> getUserIds() {
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class).select(SysUser::getUserId);
        List<SysUser> sysUsers = oldUserMapper.selectList(wrapper);
        return sysUsers.stream().map(SysUser::getUserId).toList();
    }

    @Override
    public SysUser selectUserByWxUserId(String wxUserId) {
        return oldUserMapper.selectUserByWxUserId(wxUserId);
    }

    @Override
    public SysUser getSysUser(SysUser user) {
        SysUser sysUser = oldUserMapper.getOneOnly(user);
        if (sysUser != null) {
            List<Long> roleIds = roleMapper.selectRoleListByUserId(user.getUserId());
            sysUser.setRoleIds(roleIds.toArray(new Long[0]));
        }
        return sysUser;
    }

    @Override
    public List<SysUser> getSysUsers(SysUser user) {
        return oldUserMapper.list(user);
    }

    /**
     * 用户注册之后同步手机号
     *
     * @param code  企业微信授权码
     * @param state cpId-changeType
     */
    @Override
    public void updateUserByWxOauth2(String code, String state) {
        String[] split = state.split("-");
        if (split.length < 1) {
            log.info("User oauth2 failed, can not find param: cpId.");
            return;
        }
        String cpId = split[0];
        WxCpService cpService = wxcpService.getWxCpService(wxCpProperties.getDefaultAgentId());
        WxCpOAuth2Service oauth2Service = cpService.getOauth2Service();
        WxCpOauth2UserInfo userInfo;
        WxCpUserDetail userDetail = null;
        try {
            userInfo = oauth2Service.getUserInfo(code);
            log.info("授权用户信息：{}", userInfo);
            if (Objects.nonNull(userInfo) && userInfo.getUserTicket() != null) {
                userDetail = oauth2Service.getUserDetail(userInfo.getUserTicket());
                log.info("用户详情：{}", userDetail);
            }
        } catch (WxErrorException e) {
            log.info("获取用户授权信息失败,code:{}, state: {},error：{}", code, state, e.getMessage());
        }
        if (Objects.nonNull(userDetail)) {
            log.info("wx user info detail:{}", JSON.toJSONString(userDetail));
            SysUser userByMobile = getSysUser(SysUser.builder().phonenumber(userDetail.getMobile()).build());
            if (userByMobile != null) {
                log.info("Ignore update user's mobile, mobile {} is exist", userDetail.getMobile());
                return;
            }
            SysUser sysUser = getSysUser(SysUser.builder().mainWxcpId(cpId).wxUserId(userDetail.getUserId()).build());
            if (sysUser == null) {
                log.info("Ignore update user's mobile, user not exist in system.");
            } else {
                WxCpUser wxCpUser = null;
                try {
                    wxCpUser = cpService.getUserService().getById(userDetail.getUserId());
                } catch (WxErrorException e) {
                    throw new RuntimeException(e);
                }
                SysUser updateUser = SysUser.builder().userId(sysUser.getUserId()).userName(sysUser.getUserName()).nickName(wxCpUser.getName()).phonenumber(userDetail.getMobile()).build();
                updateUser(updateUser);
            }
        }
    }

}
