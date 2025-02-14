package com.greenstone.mes.oa.application.service.impl;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.oa.application.dto.PassedApprovalQuery;
import com.greenstone.mes.oa.application.dto.attendance.result.AttendanceDetailResult;
import com.greenstone.mes.oa.application.helper.excel.AttendanceExcelHelper;
import com.greenstone.mes.oa.application.service.AttendanceExportService;
import com.greenstone.mes.oa.domain.entity.ApprovalVacation;
import com.greenstone.mes.oa.domain.entity.AttendanceResult;
import com.greenstone.mes.oa.domain.repository.AttendanceResultRepository;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/30 15:20
 */
@Service
public class AttendanceExportServiceImpl implements AttendanceExportService {

    private final AttendanceExcelHelper attendanceExcelHelper;
    private final AttendanceResultRepository attendanceResultRepository;
    private final WxApprovalRepository approvalRepository;

    public AttendanceExportServiceImpl(AttendanceExcelHelper attendanceExcelHelper, AttendanceResultRepository attendanceResultRepository,
                                       WxApprovalRepository approvalRepository) {
        this.attendanceExcelHelper = attendanceExcelHelper;
        this.attendanceResultRepository = attendanceResultRepository;
        this.approvalRepository = approvalRepository;
    }

    @Override
    public XSSFWorkbook makeAbsentStatExcel(Date start, Date end, CpId cpId) {
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(start, end, cpId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeAbsenteeismExcel(workbook, start, end, cpId, resultList);
    }

    @Override
    public XSSFWorkbook makeExtraWorkStatExcel(Date start, Date end, CpId cpId) {
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(start, end, cpId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        attendanceExcelHelper.makeExtraWorkSheet(workbook, start, end, cpId, resultList);
        return attendanceExcelHelper.makeNightShiftSheet(workbook, start, end, cpId, resultList);
    }

    @Override
    public XSSFWorkbook makeVacationStatExcel(Date start, Date end, CpId cpId) {
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(start, end, cpId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeVacationSheet(workbook, start, end, cpId, resultList);
    }

    @Override
    public XSSFWorkbook makeOutsourceStatExcel(Date start, Date end, CpId cpId) {
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(start, end, cpId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeOutSourceStatExcel(workbook, start, end, cpId, resultList);
    }

    @Override
    public XSSFWorkbook makeCheckinDetailExcel(Date start, Date end, CpId cpId) {
        List<AttendanceResult> resultList = attendanceResultRepository.listResult(start, end, cpId);
        List<AttendanceDetailResult> excelDataList = attendanceExcelHelper.makeCheckinDetailExcelData(start, end, cpId, resultList);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeCheckinDetailWorkSheet(workbook, excelDataList);
    }

    @Override
    public XSSFWorkbook makeVacationRecordExcel(Date start, Date end, CpId cpId) {
        PassedApprovalQuery query = PassedApprovalQuery.builder().cpId(cpId).start(start).end(end).build();
        List<ApprovalVacation> approvalVacations = approvalRepository.listPassedApprovalVacation(query);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeLeaveApprovalExcel(workbook, cpId, approvalVacations);
    }

    @Override
    public XSSFWorkbook makeWorkTimeStatExcel(Date start, Date end, CpId cpId, boolean allDept) {
        List<AttendanceResult> attendanceResultProds = attendanceResultRepository.listResult(start, end, cpId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        return attendanceExcelHelper.makeWorkTimeSheet(workbook, start, end, cpId, attendanceResultProds, allDept);
    }
}
