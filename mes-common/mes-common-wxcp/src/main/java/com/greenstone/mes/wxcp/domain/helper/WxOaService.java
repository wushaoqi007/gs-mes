package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.infrastructure.config.WxCpProperties;
import me.chanjar.weixin.cp.bean.oa.*;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/25 8:19
 */

public interface WxOaService {

    WxCpApprovalDetailResult getApprovalDetail(CpId cpId, SpNo spNo);

    List<String> listApprovalNo(CpId cpId, Date startTime, Date endTime, WxCpProperties.SpTemplate template);

    List<WxCpCheckinSchedule> listUserSchedules(CpId cpId, Date startTime, Date endTime, List<String> userList);

    void setUserSchedules(CpId cpId, WxCpSetCheckinSchedule checkinSchedule);

    List<WxCpCheckinData> listCheckinData(CpId cpId, Date startTime, Date endTime, List<String> userList);

    List<WxCpCheckinSchedule> listSchedule(CpId cpId, Date startTime, Date endTime, List<String> userList);

    List<WxCpCropCheckinOption> listCropCheckinOption(CpId cpId);

}
