package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.enums.CheckinType;
import com.greenstone.mes.oa.infrastructure.enums.RemitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/11/28 13:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRemit {

    private CpId cpId;

    private WxUserId userId;

    private Date day;

    private Long time;

    private CheckinType checkinType;

    private RemitType remitType;

}
