package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaWxScheduleDO;
import com.greenstone.mes.oa.domain.entity.Schedule;
import com.greenstone.mes.wxcp.domain.types.CpId;

import java.util.Date;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-06-29-9:36
 */
public interface OaWxScheduleService extends IServiceWrapper<OaWxScheduleDO> {

    List<Schedule> listSchedule(CpId cpId, Date startTime, Date endTime, List<String> userList);

    List<OaWxScheduleDO> listScheduleDO(CpId cpId, Date startTime, Date endTime, List<String> userList);

}
