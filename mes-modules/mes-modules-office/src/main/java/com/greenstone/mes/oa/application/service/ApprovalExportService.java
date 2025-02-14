package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.oa.application.dto.attendance.result.LeaveApprovalExport;

import java.util.Date;
import java.util.List;

public interface ApprovalExportService {

    List<LeaveApprovalExport> getLeaveApprovalExportData(Date start, Date end, String cpId);
}
