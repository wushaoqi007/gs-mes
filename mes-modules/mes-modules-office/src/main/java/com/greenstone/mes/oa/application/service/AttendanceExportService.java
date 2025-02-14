package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.application.dto.attendance.result.AttendanceDetailResult;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/30 15:21
 */

public interface AttendanceExportService {

    XSSFWorkbook makeAbsentStatExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeExtraWorkStatExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeVacationStatExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeOutsourceStatExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeCheckinDetailExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeVacationRecordExcel(Date start, Date end, CpId cpId);

    XSSFWorkbook makeWorkTimeStatExcel(Date start, Date end, CpId cpId, boolean allDept);
}
