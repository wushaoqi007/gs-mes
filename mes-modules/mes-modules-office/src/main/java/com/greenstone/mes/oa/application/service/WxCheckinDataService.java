package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.request.SyncCheckinDataCmd;
import me.chanjar.weixin.cp.bean.oa.WxCpCheckinData;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-07-11-11:02
 */
public interface WxCheckinDataService {
    List<WxCpCheckinData> listCheckinData(CpId cpId, Date startTime, Date endTime, List<String> userList);

    void syncCheckData(SyncCheckinDataCmd syncCheckinDataCmd);
}
