package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.dto.OaWxUserImportDto;
import com.greenstone.mes.oa.request.OaSyncApprovalCmd;
import com.greenstone.mes.system.dto.result.UserAddResult;
import com.greenstone.mes.wxcp.domain.types.CpId;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-08-9:52
 */
public interface WxSyncService {

    UserAddResult addUserByPhoneNum(String phoneNum);
    UserAddResult addUserByWxUserId(String cpId, String wxUserId);

    void clearDeletedUser();

    void clearDeletedUser(CpId cpId);

    void bindWxCpWithPhoneNum();

    void syncWxDeptWithCpid(CpId cpId);

    /**
     * 同步部门及人员
     */
    void syncWxDeptWithConfig();

    /**
     * 同步多个企业微信的成员
     */
    void syncWxUserWithConfig();

    /**
     * 同步审批
     *
     */
    void syncApproval(OaSyncApprovalCmd oaSyncApprovalCmd);

    /**
     * 同步人员排班
     *
     * @param startTime
     * @param endTime
     */
    void syncSchedule(Date startTime, Date endTime);

    /**
     * 通过导入同步通讯录人员
     *
     * @param cpId          企业id
     * @param importReqList 导入数据
     */
    void importSyncWxUser(String cpId, List<OaWxUserImportDto> importReqList);

    void syncApprovalOfAuditing(OaSyncApprovalCmd oaSyncApprovalCmd);
}
