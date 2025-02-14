package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-08-8:13
 */
@Component
public class RemoteSystemFallbackFactory implements FallbackFactory<RemoteSystemService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteSystemFallbackFactory.class);

    @Override
    public RemoteSystemService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteSystemService() {
            @Override
            public R<List<SysDept>> getDeptList(String name) {
                return R.fail("获取部门信息失败:" + throwable.getMessage());
            }

            @Override
            public R<SysDept> getDeptInfo(String name) {
                return R.fail("获取部门信息失败:" + throwable.getMessage());
            }

            @Override
            public R<List<SysUser>> listAllUser() {
                return R.fail("获取用户信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> insertDept(SysDept dept) {
                return R.fail("新增部门信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> updateDept(SysDept dept) {
                return R.fail("修改部门信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> insertUser(SysUser user) {
                return R.fail("新增用户信息失败:" + throwable.getMessage());
            }

            @Override
            public void updateUser(SysUser user) {
            }

            @Override
            public R<SysUser> basicInfo(String username) {
                return R.fail("获取用户基础信息失败:" + throwable.getMessage());
            }

            @Override
            public R<String> getConfigValueByKey(String configKey) {
                return R.fail("获取参数值失败:" + throwable.getMessage());
            }

            @Override
            public SerialNoR getNextSn(SerialNoNextCmd nextCmd) {
                return null;
            }

            @Override
            public SerialNoR nextShortSn(SerialNoNextCmd nextCmd) {
                return null;
            }

            @Override
            public SerialNoR getNextCn(SerialNoNextCmd nextCmd) {
                return null;
            }

            @Override
            public void sendSysMsg(MessageSaveCmd messageSaveCmd) {

            }

            @Override
            public SysUser getUserPublicInfo(Long userId) {
                return null;
            }

            @Override
            public void sendMessage(MessageSaveCmd messageSaveCmd) {

            }

            @Override
            public SysUser getUserInfoByWxUserId(String wxUserId) {
                return null;
            }


        };
    }
}
