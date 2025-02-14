package com.greenstone.mes.system.api.factory;

import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.auth.UserLoginAuth;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.MemberFunctionResult;
import com.greenstone.mes.system.dto.result.MemberNavigationResult;
import com.greenstone.mes.system.dto.result.UserPermissionResult;
import com.greenstone.mes.system.dto.result.UserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {
            @Override
            public UserLoginAuth getUserByLoginUsername(String loginUsername) {
                return null;
            }

            @Override
            public SysUser addUser(SysUser user) {
                return null;
            }

            @Override
            public SysUser updateUser(SysUser user) {
                return null;
            }

            @Override
            public List<SysUser> getUsersWithoutEmpNo() {
                return null;
            }

            @Override
            public User getByWx(String wxCpId, String wxUserId) {
                return null;
            }

            @Override
            public User getById(Long userId) {
                return null;
            }

            @Override
            public User getByMail(String mail) {
                return null;
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source) {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public R<String> getUserNameById(Long userId, String source) {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public String getNickName(Long userId, String source) {
                return null;
            }

            @Override
            public SysUser basicInfo(String username) {
                return null;
            }

            @Override
            public SysUser userinfo(Long userId) {
                return null;
            }

            @Override
            public void changeStatus(SysUser user) {

            }

            @Override
            public SysUser getUser(SysUser user) {
                return null;
            }

            @Override
            public List<SysUser> getUsers(SysUser user) {
                return null;
            }

            @Override
            public void remove(Long[] userIds) {

            }

            @Override
            public List<SysUser> listAll() {
                return null;
            }

            @Override
            public List<UserResult> gsUserBriefs(UserQuery query) {
                return null;
            }

            @Override
            public List<UserResult> userWorkwxInfos(UserQuery query) {
                return null;
            }

            @Override
            public void basicsEdit(SysUser user) {

            }

            @Override
            public List<UserPermissionResult> getUserPermissions(Long userId) {
                return null;
            }

            @Override
            public List<MemberNavigationResult> getUserNavigations(Long userId) {
                return null;
            }

            @Override
            public List<MemberFunctionResult> getUserFunctions(Long userId) {
                return null;
            }

        };
    }
}
