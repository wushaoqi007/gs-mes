package com.greenstone.mes.base.api;

import com.greenstone.mes.base.api.factory.RemoteOaSyncFallbackFactory;
import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.wxcp.cmd.WorkwxOauth2Cmd;
import com.greenstone.mes.oa.dto.AttendanceCalcCommand;
import com.greenstone.mes.oa.request.*;
import com.greenstone.mes.oa.response.WxLoginQrCodeR;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.system.api.model.LoginUser;
import com.greenstone.mes.system.dto.result.UserAddResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 系统远程调用接口
 *
 * @author wushaoqi
 * @date 2022-06-08-8:15
 */
@FeignClient(contextId = "remoteOaService", value = ServiceNameConstants.OFFICE_SERVICE, fallbackFactory = RemoteOaSyncFallbackFactory.class)
public interface RemoteOaService {


    /**
     * 同步企业微信部门
     */
    @PostMapping("/sync/syncWxDept")
    R<String> syncWxDept();

    /**
     * 同步企业微信成员
     */
    @PostMapping("/sync/syncWxUser")
    R<String> syncWxUser();


    /**
     * 同步企业微信审批
     */
    @PostMapping("/sync/approval")
    R<String> syncApproval(@RequestBody OaSyncApprovalCmd syncSearchReq);

    @PostMapping("/sync/approval/auditing")
    R<String> syncApprovalOfAuditing(@RequestBody OaSyncApprovalCmd syncSearchReq);

    /**
     * 同步企业微信人员排班信息
     */
    @PostMapping("/sync/schedule")
    R<String> syncSchedule(@RequestBody OaSyncApprovalCmd syncSearchReq);

    @PostMapping("/sync/checkinData")
    R<String> syncCheckinData(@RequestBody SyncCheckinDataCmd syncCheckinDataCmd);

    @PostMapping("/attendance/remind/yesterday")
    R<String> remindYesterday();

    @PostMapping("/wx/cp/msg/send")
    R<String> sendMsgToWx(@RequestBody WxMsgSendCmd msgSendReq);

    @PostMapping("/attendance/calc/yesterday")
    void calcYesterdayAttendance();

    @PostMapping("/attendance/calc/backend")
    void calcAndSaveAsync(@RequestBody AttendanceCalcCommand command);

    @PostMapping("/sync/wxUser/phone")
    UserAddResult addUserByPhoneNum(@RequestBody SysUser sysUser);

    @GetMapping("/wx/login/qrCode/{cpId}")
    WxLoginQrCodeR qrCode(@PathVariable("cpId") String cpId);

    @PostMapping("/wx/login/user")
    LoginUser qrCodeLogin(@RequestBody WorkwxOauth2Cmd loginCallBackCmd);
}
