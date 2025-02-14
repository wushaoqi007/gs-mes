package com.greenstone.mes.oa.application.dto;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
public class ApprovalCorrectionImportDTO {

    private CpId cpId;

    private SpNo spNo;

    private Date applyTime;

    private String userName;

    private String userId;

    private String reason;

    private Date correctionTime;

    private String proveFile;

    private ApprovalStatus status;

    private String remark;

    public boolean hasMediaFile() {
        return (proveFile != null && !"无".equals(proveFile)) || (remark != null && remark.contains("附件"));
    }

}
