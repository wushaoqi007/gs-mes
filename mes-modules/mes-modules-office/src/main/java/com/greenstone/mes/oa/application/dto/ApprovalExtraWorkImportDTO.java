package com.greenstone.mes.oa.application.dto;

import com.alibaba.fastjson2.JSON;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
public class ApprovalExtraWorkImportDTO {

    private CpId cpId;

    private SpNo spNo;

    private Date applyTime;

    private String userName;

    private WxUserId userId;

    private String submitType;

    private String reason;

    private Date startTime;

    private Date endTime;

    private ApprovalStatus status;

    private String remark;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
