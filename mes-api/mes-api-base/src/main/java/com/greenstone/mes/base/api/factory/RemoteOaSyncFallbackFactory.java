package com.greenstone.mes.base.api.factory;

import com.greenstone.mes.base.api.RemoteOaService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.oa.dto.AttendanceCalcCommand;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import com.greenstone.mes.oa.response.WxLoginQrCodeR;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.dto.result.UserAddResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author wushaoqi
 * @date 2022-06-08-8:13
 */
@Component
public class RemoteOaSyncFallbackFactory implements FallbackFactory<RemoteOaService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteOaSyncFallbackFactory.class);

    @Override
    public RemoteOaService create(Throwable throwable) {
        log.error("基础配置服务调用失败:{}", throwable.getMessage());
        return new RemoteOaService() {
            @Override
            public R<String> syncWxDept() {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncWxUser() {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncApproval(OaSyncApprovalCmd oaSyncApprovalCmd) {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncApprovalOfAuditing(OaSyncApprovalCmd syncSearchReq) {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncSchedule(OaSyncApprovalCmd oaSyncApprovalCmd) {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> syncCheckinData(SyncCheckinDataCmd syncCheckinDataCmd) {
                return R.fail("同步失败:" + throwable.getMessage());
            }

            @Override
            public R<String> remindYesterday() {
                return R.fail("考勤异常提醒失败");
            }

            @Override
            public R<String> sendMsgToWx(WxMsgSendCmd msgSendReq) {
                return R.fail("消息发送失败");
            }

            @Override
            public void calcYesterdayAttendance() {

            }

            @Override
            public void calcAndSaveAsync(AttendanceCalcCommand command) {

            }

            @Override
            public UserAddResult addUserByPhoneNum(SysUser sysUser) {
                return UserAddResult.builder().success(false).build();
            }

            @Override
            public WxLoginQrCodeR qrCode(String cpId) {
                return null;
            }

            @Override
            public LoginUser qrCodeLogin(WorkwxOauth2Cmd loginCallBackCmd) {
                return null;
            }

        };
    }
}
