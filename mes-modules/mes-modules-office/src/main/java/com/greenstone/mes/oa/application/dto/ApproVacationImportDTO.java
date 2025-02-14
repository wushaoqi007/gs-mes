package com.greenstone.mes.oa.application.dto;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.enums.VacationType;
import lombok.Data;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/12/9 10:41
 */
@Data
public class ApproVacationImportDTO {

    private CpId cpId;

    private SpNo spNo;

    private Date applyTime;

    private String userName;

    private WxUserId userId;

    private VacationType type;

    private Date startTime;

    private Date endTime;

    private String reason;

    private String attachment;

    private ApprovalStatus status;

    private String remark;

    public boolean hasMediaFile() {
        return (attachment != null && !"无".equals(attachment)) || (remark != null && remark.contains("附件"));
    }

}
