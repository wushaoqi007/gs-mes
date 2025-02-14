package com.greenstone.mes.oa.application.helper.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.assembler.AttendanceAssembler;
import com.greenstone.mes.oa.application.dto.PassedApprovalQuery;
import com.greenstone.mes.oa.application.dto.attendance.result.AttendanceDetailResult;
import com.greenstone.mes.oa.application.helper.WorkWxHelper;
import com.greenstone.mes.oa.domain.entity.ApprovalExtraWork;
import com.greenstone.mes.oa.domain.entity.ApprovalVacation;
import com.greenstone.mes.oa.domain.entity.AttendanceResult;
import com.greenstone.mes.oa.domain.entity.WxDept;
import com.greenstone.mes.oa.domain.helper.AttendanceHelper;
import com.greenstone.mes.oa.domain.repository.WxApprovalRepository;
import com.greenstone.mes.oa.infrastructure.enums.AttendanceExceptionType;
import com.greenstone.mes.oa.infrastructure.enums.WxCp;
import com.greenstone.mes.oa.infrastructure.util.Periods;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxMediaService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.FileId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gu_renkai
 * @date 2022/11/30 15:50
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AttendanceExcelHelper {

    private final WxUserService externalWxUserService;
    private final WxDeptService externalWxDeptService;
    private final AttendanceHelper attendanceHelper;
    private final WxMediaService externalWxMediaService;
    private final AttendanceAssembler attendanceAssembler;
    private final WorkWxHelper workWxHelper;
    private final WxApprovalRepository approvalRepository;


    /**
     * 缺勤表
     */
    public XSSFWorkbook makeAbsenteeismExcel(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList) {
        Sheet sheet = wb.createSheet("缺勤记录");

        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(daysBeginTimeList.get(0) * 1000));
        // 第一行，标题行
        Row firstRow = sheet.createRow(0);
        Cell cell = firstRow.createCell(0);
        cell.setCellValue(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "迟到、早退、未打卡、补卡统计表");
        // 合并第一行的单元格，列数 = 查询天数 * 4 + 3 + 3
        int totalColumnNum = daysBeginTimeList.size() * 4 + 4 + 3;
        CellRangeAddress firstRowRange = new CellRangeAddress(0, 0, 0, totalColumnNum);
        sheet.addMergedRegion(firstRowRange);
        // 第二行，表头：日期
        int secondRowColumn = 4;
        Row secondRow = sheet.createRow(1);
        for (Long beginTime : daysBeginTimeList) {
            Cell dayCell = secondRow.createCell(secondRowColumn);
            calendar.setTime(new Date(beginTime * 1000));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dayCell.setCellValue(day);
            // 合并第二行的单元格
            CellRangeAddress secondRowRange = new CellRangeAddress(1, 1, secondRowColumn, secondRowColumn + 3);
            sheet.addMergedRegion(secondRowRange);
            secondRowColumn += 4;
        }
        // 第二行，前三列的表头，是固定的
        Cell numberCell = secondRow.createCell(0);
        numberCell.setCellValue("序号");
        CellRangeAddress numberRange = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(numberRange);
        Cell departCell = secondRow.createCell(1);
        departCell.setCellValue("部门");
        CellRangeAddress departRange = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(departRange);
        Cell nameCell = secondRow.createCell(2);
        nameCell.setCellValue("姓名");
        CellRangeAddress nameRange = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(nameRange);
        Cell gongHaoCell = secondRow.createCell(3);
        gongHaoCell.setCellValue("工号");
        CellRangeAddress gongHaoRange = new CellRangeAddress(1, 2, 3, 3);
        sheet.addMergedRegion(gongHaoRange);
        // 第二行，最后一列的表头
        int statCellColumn = 4 + daysBeginTimeList.size() * 4;
        Cell statCell = secondRow.createCell(statCellColumn);
        statCell.setCellValue("统计");
//        CellRangeAddress statRange = new CellRangeAddress(1, 1, statCellColumn, statCellColumn + 2);
//        sheet.addMergedRegion(statRange);
        // 第三行，二级表头：缺卡类型
        int thirdRowColumn = 3;
        Row thirdRow = sheet.createRow(2);
        for (int i = 0; i < daysBeginTimeList.size(); i++) {
            Cell typeCell1 = thirdRow.createCell(thirdRowColumn + 1);
            typeCell1.setCellValue("迟到");
            Cell typeCell2 = thirdRow.createCell(thirdRowColumn + 2);
            typeCell2.setCellValue("早退");
            Cell typeCell3 = thirdRow.createCell(thirdRowColumn + 3);
            typeCell3.setCellValue("未打卡");
            Cell typeCell4 = thirdRow.createCell(thirdRowColumn + 4);
            typeCell4.setCellValue("补卡");
            thirdRowColumn += 4;
        }
        // 第三行，二级表头，最后三列 固定的
        Cell statCell1 = thirdRow.createCell(statCellColumn);
        statCell1.setCellValue("迟到早退");
        Cell statCell2 = thirdRow.createCell(statCellColumn + 1);
        statCell2.setCellValue("未打卡");
        Cell statCell3 = thirdRow.createCell(statCellColumn + 2);
        statCell3.setCellValue("补卡");

        if (CollUtil.isEmpty(resultList)) {
            return wb;
        }
        // 缺勤数据
        int number = 0; // 序号字段值
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();
        for (WxCpUser wxCpUser : wxCpUsers) {
            number++;
            // 数据行
            Row dataRow = sheet.createRow(number + 2);
            // 序号字段值
            Cell numberValue = dataRow.createCell(0);
            numberValue.setCellValue(number);
            // 部门字段值
            Cell deptValue = dataRow.createCell(1);
            WxCpDepart wxCpDepart = externalWxDeptService.getDept(cpId, new WxDeptId(wxCpUser.getDepartIds()[0]));
            WxDept dept = attendanceAssembler.toWxDept(wxCpDepart);
            attendanceHelper.setDeptFullName(dept, cpId, wxCpDepart);
            deptValue.setCellValue(dept.getFullName());
            // 用户名称字段值
            Cell userNameValue = dataRow.createCell(2);
            userNameValue.setCellValue(wxCpUser.getName());
            Cell gongHaoValue = dataRow.createCell(3);
            gongHaoValue.setCellValue(workWxHelper.getEmployeeNo(wxCpUser));
            List<AttendanceResult> attendanceResults = resultList.stream().filter(r -> r.getUserId().id().equals(wxCpUser.getUserId())).toList();
            if (CollUtil.isEmpty(attendanceResults)) {
                continue;
            }

            int dayNumber = 0;
            int lateAndLearyTimes = 0;
            int lackCheckInTimes = 0;
            int punchCorrectionTimes = 0;
            for (Long dayTimeSec : daysBeginTimeList) {
                dayNumber++; // 记录迭代次数
                AttendanceResult result = attendanceResults.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec).findFirst().orElse(null);
                if (result == null) {
                    continue;
                }
                Calendar c = Calendar.getInstance();
                c.setTime(result.getDay());

                // 迟到
                if (result.getLateEarlyRemitTimes() != null && result.getLateEarlyRemitTimes() > 0) {
                    Cell cellLate = dataRow.createCell(dayNumber * 4);
                    cellLate.setCellValue(1);
                    lateAndLearyTimes++;
                }
                // 早退
                if (result.getLateEarlyRemitTimes() != null && result.getLateEarlyRemitTimes() > 1) {
                    Cell cellLeary = dataRow.createCell(dayNumber * 4 + 1);
                    cellLeary.setCellValue(1);
                    lateAndLearyTimes++;
                }
                // 未打卡
                if (result.getExceptionType() != null &&
                        (result.getExceptionType() == AttendanceExceptionType.ABSENT || result.getExceptionType() == AttendanceExceptionType.LACK)) {
                    Cell lackCheckInCell = dataRow.createCell(dayNumber * 4 + 2);
                    lackCheckInCell.setCellValue(1);
                    lackCheckInTimes++;
                }
                // 补卡
                if (result.getCorrectRemitTimes() != null && result.getCorrectRemitTimes() > 0) {
                    Cell punchCorrectionCell = dataRow.createCell(dayNumber * 4 + 3);
                    punchCorrectionCell.setCellValue(result.getCorrectRemitTimes());
                    punchCorrectionTimes += result.getCorrectRemitTimes();
                }
            }
            Cell lateAndLearyTimesCell = dataRow.createCell(dayNumber * 4 + 4);
            lateAndLearyTimesCell.setCellValue(lateAndLearyTimes);
            Cell lackCheckInTimesCell = dataRow.createCell(dayNumber * 4 + 5);
            lackCheckInTimesCell.setCellValue(lackCheckInTimes);
            Cell punchCorrectionTimesCell = dataRow.createCell(dayNumber * 4 + 6);
            punchCorrectionTimesCell.setCellValue(punchCorrectionTimes);

        }
        return wb;
    }

    /**
     * 请假表
     */
    public XSSFWorkbook makeVacationSheet(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList) {
        Sheet sheet = wb.createSheet("请假");

        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);
        int dayNum = daysBeginTimeList.size();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        // 第一行 标题
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(sdf.format(start) + "-" + sdf.format(end));

        // 第二行。序号、姓名、星期
        Row row1 = sheet.createRow(1);
        // 第三行。日期，假期类型
        Row row2 = sheet.createRow(2);

        // 序号
        row1.createCell(0).setCellValue("序号");
        CellRangeAddress numberRegion = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(numberRegion);
        // 姓名
        row1.createCell(1).setCellValue("姓名");
        CellRangeAddress nameRegion = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(nameRegion);
        // 姓名
        row1.createCell(2).setCellValue("工号");
        CellRangeAddress gongHaoRegion = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(gongHaoRegion);
        // 日期和星期
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < daysBeginTimeList.size(); i++) {
            calendar.setTime(new Date(daysBeginTimeList.get(i) * 1000));
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Cell cayOfWeekCell = row1.createCell(i + 3);
            cayOfWeekCell.setCellValue(attendanceHelper.getChineseDayOfWeek(dayOfWeek));
            Cell cayOfMonthCell = row2.createCell(i + 3);
            cayOfMonthCell.setCellValue(dayOfMonth);
        }
        // 假期类型
        row2.createCell(dayNum + 3).setCellValue("事假");
        row2.createCell(dayNum + 4).setCellValue("调休假");
        row2.createCell(dayNum + 5).setCellValue("年假");
        row2.createCell(dayNum + 6).setCellValue("病假");
        row2.createCell(dayNum + 7).setCellValue("婚假");
        row2.createCell(dayNum + 8).setCellValue("产假");
        row2.createCell(dayNum + 9).setCellValue("陪产假");
        row2.createCell(dayNum + 10).setCellValue("其他");
        row2.createCell(dayNum + 11).setCellValue("缺勤");

        DecimalFormat df = new DecimalFormat("##0.00");
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        // 内容
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();
        int rowNumber = 3;
        for (WxCpUser user : wxCpUsers) {
            double personalLeave = 0; // 事假
            double timeOffInLieuLeave = 0; // 调休假
            double annualLeave = 0; // 年假
            double sickLeave = 0; // 病假
            double maritalLeave = 0; // 婚假
            double maternityLeave = 0; // 产假
            double accompanyingMaternityLeave = 0; // 陪产假
            double other = 0; // 其他
            double lack = 0; // 缺勤
            int dayNumber = 0;
            Row dataRow = sheet.createRow(rowNumber);
            dataRow.createCell(0).setCellValue(rowNumber - 2);
            dataRow.createCell(1).setCellValue(user.getName());
            dataRow.createCell(2).setCellValue(workWxHelper.getEmployeeNo(user));
            for (Long dayTimeSec : daysBeginTimeList) {
                AttendanceResult result = resultList.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec && r.getUserId().id().equals(user.getUserId())).findFirst().orElse(null);
                if (result == null) {
                    dayNumber++;
                    continue;
                }
                Cell dataCell = dataRow.createCell(dayNumber + 3);
                String content = "";
                if (result.getExceptionType() != null && result.getExceptionTime() > 0) {
                    double absenteeismTime = Double.valueOf(result.getExceptionTime()) / 60 / 60;
                    content = "缺勤" + numberFormat.format(absenteeismTime);
                    lack += absenteeismTime;
                }
                if (result.getVacationTime() != null) {
                    double leaveTime = Double.valueOf(result.getVacationTime()) / 60 / 60;
                    if (StrUtil.isNotEmpty(content)) {
                        content = content + "\n";
                    }
                    content = content + result.getVacationType() + numberFormat.format(leaveTime);
                    if (!"0.0".equals(content)) {
                        switch (result.getVacationType()) {
                            case "事假" -> personalLeave += leaveTime;
                            case "调休假" -> timeOffInLieuLeave += leaveTime;
                            case "年假" -> annualLeave += leaveTime;
                            case "病假" -> sickLeave += leaveTime;
                            case "婚假" -> maritalLeave += leaveTime;
                            case "产假" -> maternityLeave += leaveTime;
                            case "陪产假" -> accompanyingMaternityLeave += leaveTime;
                            default -> other += leaveTime;
                        }
                    }
                }
                dataCell.setCellValue(content);
                dayNumber++;
            }
            dataRow.createCell(dayNumber + 3).setCellValue(personalLeave == 0 ? null : numberFormat.format(personalLeave));
            dataRow.createCell(dayNumber + 4).setCellValue(timeOffInLieuLeave == 0 ? null : numberFormat.format(timeOffInLieuLeave));
            dataRow.createCell(dayNumber + 5).setCellValue(annualLeave == 0 ? null : numberFormat.format(annualLeave));
            dataRow.createCell(dayNumber + 6).setCellValue(sickLeave == 0 ? null : numberFormat.format(sickLeave));
            dataRow.createCell(dayNumber + 7).setCellValue(maritalLeave == 0 ? null : numberFormat.format(maritalLeave));
            dataRow.createCell(dayNumber + 8).setCellValue(maternityLeave == 0 ? null : numberFormat.format(maternityLeave));
            dataRow.createCell(dayNumber + 9).setCellValue(accompanyingMaternityLeave == 0 ? null : numberFormat.format(accompanyingMaternityLeave));
            dataRow.createCell(dayNumber + 10).setCellValue(other == 0 ? null : numberFormat.format(other));
            dataRow.createCell(dayNumber + 11).setCellValue(lack == 0 ? null : numberFormat.format(lack));
            rowNumber++;
        }
        // 边框
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i < rowNumber; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < daysBeginTimeList.size() + 12; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(cellStyle);
            }
        }
        return wb;
    }

    public XSSFWorkbook makeExtraWorkSheet(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList) {
        Sheet sheet = wb.createSheet("加班");
        // 设置黄色单元格样式
        XSSFCellStyle yellowCellStyle = wb.createCellStyle();
        // 设置背景色
        yellowCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);
        int dayNum = daysBeginTimeList.size();
        // 第一行 标题
        Row titleRow = sheet.createRow(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        titleRow.createCell(0).setCellValue(sdf.format(start) + "-" + sdf.format(end));

        // 第二行 表头
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        // 序号
        row1.createCell(0).setCellValue("序号");
        CellRangeAddress numberRegion = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(numberRegion);
        // 姓名
        row1.createCell(1).setCellValue("姓名");
        CellRangeAddress nameRegion = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(nameRegion);
        // 工号
        row1.createCell(2).setCellValue("工号");
        CellRangeAddress gongHaoRegion = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(gongHaoRegion);
        Calendar c = Calendar.getInstance();
        // 合计
        row1.createCell(dayNum + 3).setCellValue("合计");
        CellRangeAddress totalRegion = new CellRangeAddress(1, 2, dayNum + 3, dayNum + 3);
        sheet.addMergedRegion(totalRegion);
        // 日期和星期
        int dayNumber = 0;
        for (Long dayStartTime : daysBeginTimeList) {
            c.setTime(new Date(dayStartTime * 1000));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Cell cayOfWeekCell = row1.createCell(dayNumber + 3);
            cayOfWeekCell.setCellValue(attendanceHelper.getChineseDayOfWeek(dayOfWeek));
            Cell cayOfMonthCell = row2.createCell(dayNumber + 3);
            cayOfMonthCell.setCellValue(dayOfMonth);
            dayNumber++;
        }
        // 内容
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();
        int rowNumber = 3;
        for (WxCpUser user : wxCpUsers) {
            String employeeNo = workWxHelper.getEmployeeNo(user);
            boolean isNotCountExtraWork = attendanceHelper.isNotCountExtraWork(employeeNo);

            double totalOvertime = 0;
            dayNumber = 0;
            Row dataRow = sheet.createRow(rowNumber);
            dataRow.createCell(0).setCellValue(rowNumber - 2);
            dataRow.createCell(1).setCellValue(user.getName());
            if (isNotCountExtraWork) {
                dataRow.getCell(1).setCellStyle(yellowCellStyle);
            }
            dataRow.createCell(2).setCellValue(employeeNo);

            DecimalFormat df = new DecimalFormat("##0.0");
            for (Long dayTimeSec : daysBeginTimeList) {
                AttendanceResult result = resultList.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec && r.getUserId().id().equals(user.getUserId())).findFirst().orElse(null);
                if (result != null) {
                    Cell dataCell = dataRow.createCell(dayNumber + 3);
                    if (result.getExtraWorkTime() != null) {
                        double overTime = Double.valueOf(result.getExtraWorkTime()) / 60 / 60;
                        String timeStr = df.format(overTime);
                        if (!"0.0".equals(timeStr)) {
                            dataCell.setCellValue(timeStr);
                            totalOvertime += overTime;
                        }
                    }
                }
                dayNumber++;
            }
            dataRow.createCell(dayNumber + 3).setCellValue(df.format(totalOvertime));
            rowNumber++;
        }
        // 边框
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i < rowNumber; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < dayNum + 4; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (cell.getCellStyle().getFillForegroundColor() == IndexedColors.YELLOW.getIndex()) {
                    continue;
                }
                cell.setCellStyle(cellStyle);
            }
        }
        return wb;
    }

    public XSSFWorkbook makeCheckinDetailWorkSheet(XSSFWorkbook wb, List<AttendanceDetailResult> resultList) {
        Sheet sheet = wb.createSheet("明细表");
        // 设置黄色单元格样式
        XSSFCellStyle yellowCellStyle = wb.createCellStyle();
        // 设置背景色
        yellowCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 第二行 表头
        Row row1 = sheet.createRow(0);
        // 序号
        row1.createCell(0).setCellValue("序号");
        row1.createCell(1).setCellValue("部门");
        row1.createCell(2).setCellValue("人员");
        row1.createCell(3).setCellValue("工号");
        row1.createCell(4).setCellValue("打卡日期");
        row1.createCell(5).setCellValue("打卡时间");
        row1.createCell(6).setCellValue("打卡地点");
        row1.createCell(7).setCellValue("备注");
        row1.createCell(8).setCellValue("打卡次数");
        row1.createCell(9).setCellValue("早晚班");
        row1.createCell(10).setCellValue("特殊班次");
        row1.createCell(11).setCellValue("标准打卡时间");
        row1.createCell(12).setCellValue("打卡类型");
        // 内容
        int rowNumber = 1;
        for (AttendanceDetailResult result : resultList) {
            Row dataRow = sheet.createRow(rowNumber);
            dataRow.createCell(0).setCellValue(rowNumber);
            dataRow.createCell(1).setCellValue(result.getDeptName());
            dataRow.createCell(2).setCellValue(result.getName());
            dataRow.createCell(3).setCellValue(result.getGongHao());
            dataRow.createCell(4).setCellValue(result.getCheckInDate());
            dataRow.createCell(5).setCellValue(result.getCheckInTime());
            dataRow.createCell(6).setCellValue(result.getCheckInLocation());
            dataRow.createCell(7).setCellValue(result.getRemark());
            dataRow.createCell(8).setCellValue(result.getCheckInTimes() == null ? "" : result.getCheckInTimes());
            dataRow.createCell(9).setCellValue(result.getShiftName());
            dataRow.createCell(10).setCellValue(result.getCustomShift() == null ? "" : result.getCustomShift());
            dataRow.createCell(11).setCellValue(result.getSchTime());
            dataRow.createCell(12).setCellValue(result.getCheckinType());
            boolean isNotCountExtraWork = attendanceHelper.isNotCountExtraWork(result.getGongHao());
            if (isNotCountExtraWork) {
                dataRow.getCell(2).setCellStyle(yellowCellStyle);
            }
            rowNumber++;
        }
        // 边框
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 0; i < rowNumber; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < 13; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (cell.getCellStyle().getFillForegroundColor() == IndexedColors.YELLOW.getIndex()) {
                    continue;
                }
                cell.setCellStyle(cellStyle);
            }
        }
        return wb;
    }

    public XSSFWorkbook makeNightShiftSheet(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList) {
        Sheet sheet = wb.createSheet("夜班");

        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);
        int dayNum = daysBeginTimeList.size();

        // 第一行 标题
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue(sdf.format(start) + "-" + sdf.format(end));

        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        // 序号
        row1.createCell(0).setCellValue("序号");
        CellRangeAddress numberRegion = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(numberRegion);
        // 姓名
        row1.createCell(1).setCellValue("姓名");
        CellRangeAddress nameRegion = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(nameRegion);
        // 工号
        row1.createCell(2).setCellValue("工号");
        CellRangeAddress gongHaoRegion = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(gongHaoRegion);
        Calendar c = Calendar.getInstance();
        // 合计
        row1.createCell(dayNum + 3).setCellValue("合计");
        CellRangeAddress totalRegion = new CellRangeAddress(1, 2, dayNum + 3, dayNum + 3);
        sheet.addMergedRegion(totalRegion);
        // 日期和星期
        int dayNumber = 0;
        for (Long dayTimeSec : daysBeginTimeList) {
            c.setTime(new Date(dayTimeSec * 1000));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Cell cayOfWeekCell = row1.createCell(dayNumber + 3);
            cayOfWeekCell.setCellValue(attendanceHelper.getChineseDayOfWeek(dayOfWeek));
            Cell cayOfMonthCell = row2.createCell(dayNumber + 3);
            cayOfMonthCell.setCellValue(dayOfMonth);
            dayNumber++;
        }
        // 内容
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();

        DecimalFormat df = new DecimalFormat("##0.0");
        int rowNumber = 3;
        for (WxCpUser user : wxCpUsers) {
            long totalNightShift = 0;
            dayNumber = 0;
            Row dataRow = sheet.createRow(rowNumber);
            dataRow.createCell(0).setCellValue(rowNumber - 2);
            dataRow.createCell(1).setCellValue(user.getName());
            dataRow.createCell(2).setCellValue(workWxHelper.getEmployeeNo(user));

            for (Long dayTimeSec : daysBeginTimeList) {
                dayNumber++;
                AttendanceResult result = resultList.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec && r.getUserId().id().equals(user.getUserId())).findFirst().orElse(null);
                if (result == null) {
                    continue;
                }
                Cell dataCell = dataRow.createCell(dayNumber + 2);
                if (result.getShift().isNightShift()) {
                    if (result.isTrip()) {
                        dataCell.setCellValue("外地");
                    } else {
                        dataCell.setCellValue("无锡");
                    }
                    totalNightShift++;
                }
            }
            dataRow.createCell(dayNumber + 3).setCellValue(df.format(totalNightShift));
            rowNumber++;
        }
        // 边框
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i < rowNumber; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < dayNum + 4; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(cellStyle);
            }
        }
        return wb;
    }

    public XSSFWorkbook makeOutSourceStatExcel(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList) {
        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);
        int dayNum = daysBeginTimeList.size();

        Sheet sheet = wb.createSheet("外包考勤");
        // 第一行 标题
        Row titleRow = sheet.createRow(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月");
        String month = sdf.format(start);
        titleRow.createCell(0).setCellValue("日期（" + month + "）");
        CellRangeAddress titleRegion = new CellRangeAddress(0, 0, 0, dayNum + 2);
        sheet.addMergedRegion(titleRegion);

        Row row1 = sheet.createRow(1);
        // 姓名
        row1.createCell(0).setCellValue("姓名");

        // 合计
        row1.createCell(dayNum + 1).setCellValue("合计");
        // 日期和星期
        int dayNumber = 0;
        Calendar c = Calendar.getInstance();
        for (Long dayTimeSec : daysBeginTimeList) {
            c.setTime(new Date(dayTimeSec * 1000));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            Cell cayOfDayCell = row1.createCell(dayNumber + 1);
            cayOfDayCell.setCellValue(dayOfMonth);
            dayNumber++;
        }

        // 设置黄色单元格样式
        XSSFCellStyle yellowCellStyle = wb.createCellStyle();
        // 设置蓝色单元格样式
        XSSFCellStyle blueCellStyle = wb.createCellStyle();
        // 设置背景色粉色
        yellowCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 设置背景色蓝色
        blueCellStyle.setFillForegroundColor((short) 44);
        blueCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        int rowNumber = 0;
        for (WxCpUser user : wxCpUsers) {
            double totalWorkTime = 0;

            Row dataRow = sheet.createRow(rowNumber * 3 + 2);
            dataRow.createCell(0).setCellValue(user.getName());
            Row dataRow2 = sheet.createRow(rowNumber * 3 + 3);
            Row dataRow3 = sheet.createRow(rowNumber * 3 + 4);
            // 合并每人3行
            CellRangeAddress nameRegion = new CellRangeAddress(rowNumber * 3 + 2, rowNumber * 3 + 4, 0, 0);
            sheet.addMergedRegion(nameRegion);

            dayNumber = -1;
            rowNumber++;
            DecimalFormat df = new DecimalFormat("##0.0");
            for (Long dayTimeSec : daysBeginTimeList) {
                dayNumber++;
                AttendanceResult result = resultList.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec && r.getUserId().id().equals(user.getUserId())).findFirst().orElse(null);
                if (result == null) {
                    continue;
                }
                // 上下班打卡
                Cell checkinCell = dataRow.createCell(dayNumber + 1);
                // 工作时长
                Cell workTimeCell = dataRow2.createCell(dayNumber + 1);
                // 打卡地点
                Cell locationCell = dataRow3.createCell(dayNumber + 1);

                if (result.getWorkTime() != null) {
                    double workTime = Double.valueOf(result.getWorkTime()) / 60 / 60;
                    String timeStr = df.format(workTime);
                    if (!"0.0".equals(timeStr)) {
                        workTimeCell.setCellValue(timeStr);
                        totalWorkTime += workTime;
                    }
                }

                locationCell.setCellValue(result.getCheckinLocation());


                if (result.getSignInTime() != null && result.getSignOutTime() != null) {
                    checkinCell.setCellValue(sdf2.format(result.getSignInTime()) + "-" + sdf2.format(result.getSignOutTime()));
                }
                // 标记夜班颜色黄色
                if (result.getShift().isNightShift()) {
                    checkinCell.setCellStyle(yellowCellStyle);
                    workTimeCell.setCellStyle(yellowCellStyle);
                    locationCell.setCellStyle(yellowCellStyle);
                }
                // 标记出差蓝色
                if (result.isTrip()) {
                    checkinCell.setCellStyle(blueCellStyle);
                    workTimeCell.setCellStyle(blueCellStyle);
                    locationCell.setCellStyle(blueCellStyle);
                }
            }
            dataRow2.createCell(dayNumber + 1).setCellValue(df.format(totalWorkTime));

        }
        return wb;
    }

    public XSSFWorkbook makeLeaveApprovalExcel(XSSFWorkbook wb, CpId cpId, List<ApprovalVacation> vacations) {
        // 超链接样式
        XSSFCellStyle linkStyle = wb.createCellStyle();
        XSSFFont cellFont = wb.createFont();
        cellFont.setUnderline((byte) 1);
        cellFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkStyle.setFont(cellFont);

        Sheet leaveSheet = wb.createSheet("请假");
        Sheet fileSheet = wb.createSheet("附件");

        Row titleRow = leaveSheet.createRow(0);
        titleRow.createCell(0).setCellValue("序号");
        titleRow.createCell(1).setCellValue("姓名");
        titleRow.createCell(2).setCellValue("类型");
        titleRow.createCell(3).setCellValue("开始时间");
        titleRow.createCell(4).setCellValue("结束时间");
        titleRow.createCell(5).setCellValue("理由");
        titleRow.createCell(6).setCellValue("审批人");
        titleRow.createCell(7).setCellValue("附件");

        int rowNum = 1;
        for (ApprovalVacation vacation : vacations) {
            Row dataRow = leaveSheet.createRow(rowNum);
            dataRow.createCell(0).setCellValue(rowNum);
            dataRow.createCell(1).setCellValue(vacation.getUserName());
            dataRow.createCell(2).setCellValue(vacation.getType().getName());
            dataRow.createCell(3).setCellValue(vacation.getStartTime());
            dataRow.createCell(4).setCellValue(vacation.getEndTime());
            dataRow.createCell(5).setCellValue(vacation.getReason());
            dataRow.createCell(6).setCellValue(vacation.getUserName()); // TODO 此处应为批准人而不是申请人
            if (CollUtil.isNotEmpty(vacation.allMedias())) {
                Cell fileNumCell = dataRow.createCell(7);
                fileNumCell.setCellValue(vacation.allMedias().size() + "个");

                for (WxMediaId mediaId : vacation.allMedias()) {
                    try {
                        File file = externalWxMediaService.download(cpId, new FileId(mediaId.id()));
                        Row fileRow = fileSheet.createRow(rowNum);
                        fileRow.createCell(0);

                        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
                            // 将图片写入输出流
                            BufferedImage bufferImg = ImageIO.read(file);
                            // 设置图片对应的单元格高宽
                            if (fileSheet.getColumnWidth(0) < bufferImg.getWidth()) {
                                fileSheet.setColumnWidth(0, bufferImg.getWidth());
                            }
                            fileRow.setHeight((short) bufferImg.getHeight());
                            // 设置图片超链接
                            XSSFCreationHelper creationHelper = wb.getCreationHelper();
                            XSSFHyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
                            String address = "#附件!A" + (rowNum + 1);
                            hyperlink.setAddress(address);
                            fileNumCell.setHyperlink(hyperlink);
                            // 设置样式
                            fileNumCell.setCellStyle(linkStyle);


                            // 写入图片到表格
                            ImageIO.write(bufferImg, "jpeg", byteArrayOut);
                            // 利用HSSFPatriarch将图片写入EXCEL
                            Drawing<?> patriarch = fileSheet.createDrawingPatriarch();
                            // 图片放入的单元格范围
                            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, rowNum, 1, rowNum + 1);
                            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                            // 插入图片内容
                            Picture picture = patriarch.createPicture(anchor, wb.addPicture(byteArrayOut
                                    .toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG));
                        }
                    } catch (ServiceException | IOException e) {
                        log.error("Get file from wx error", e);
                    }
                }
            }
            rowNum++;
        }
        return wb;
    }

    public List<AttendanceDetailResult> makeCheckinDetailExcelData(Date start, Date end, CpId cpId, List<AttendanceResult> attendanceResultList) {
        List<AttendanceDetailResult> detailResultList = new ArrayList<>();
        attendanceResultList.stream().collect(Collectors.groupingBy(AttendanceResult::getUserId)).forEach((uid, list) -> {
            WxCpUser wxCpUser;
            try {
                wxCpUser = externalWxUserService.getUser(cpId, uid);
            } catch (RuntimeException error) {
                log.info("考勤用户：{}未找到，不纳入考勤结果展示", uid);
                return;
            }
            WxCpDepart wxCpDepart = null;
            WxDept dept = null;
            try {
                wxCpDepart = externalWxDeptService.getDept(cpId, new WxDeptId(wxCpUser.getDepartIds()[0]));
                dept = attendanceAssembler.toWxDept(wxCpDepart);
                attendanceHelper.setDeptFullName(dept, cpId, wxCpDepart);
            } catch (Exception e) {
                log.error("获取部门错误", e);
            }
            for (AttendanceResult result : list) {
                if (Objects.nonNull(result.getSignInTime())) {
                    String dateStr = DateUtil.date(result.getSignInTime()).toDateStr();
                    String timeStr = DateUtil.date(result.getSignInTime()).toTimeStr();
                    AttendanceDetailResult detailResult = AttendanceDetailResult.builder().name(wxCpUser == null ? "" : wxCpUser.getName()).
                            deptName(dept == null ? "" : dept.getFullName()).
                            gongHao(wxCpUser == null ? "" : workWxHelper.getEmployeeNo(wxCpUser)).
                            checkInDate(dateStr).checkInTime(timeStr).
                            checkInLocation(result.getCheckinLocation()).
                            remark(result.getCheckinRemark()).
                            checkInTimes(result.getCheckinTimes() == null ? "" : result.getCheckinTimes().toString()).
                            dayBeginTime(result.getDay().getTime() / 1000).checkinTimeL(result.getSignInTime().getTime() / 1000).
                            shiftName(result.getShift().getName()).checkinType("上班打卡").schTime(DateUtil.format(result.getSchSignInTime(), "HH:mm")).customShift(result.getCustomShiftName()).build();
                    detailResultList.add(detailResult);
                }
                if (Objects.nonNull(result.getSignOutTime())) {
                    String dateStr = DateUtil.date(result.getSignOutTime()).toDateStr();
                    String timeStr = DateUtil.date(result.getSignOutTime()).toTimeStr();
                    AttendanceDetailResult detailResult = AttendanceDetailResult.builder().name(wxCpUser == null ? "" : wxCpUser.getName()).
                            deptName(dept == null ? "" : dept.getFullName()).
                            gongHao(wxCpUser == null ? "" : workWxHelper.getEmployeeNo(wxCpUser)).
                            checkInDate(dateStr).checkInTime(timeStr).
                            checkInLocation(result.getCheckinLocationSecond()).
                            remark(result.getCheckinRemarkSecond()).
                            dayBeginTime(result.getDay().getTime() / 1000).checkinTimeL(result.getSignOutTime().getTime() / 1000).
                            shiftName(result.getShift().getName()).checkinType("下班打卡").schTime(DateUtil.format(result.getSchSignOutTime(), "HH:mm")).customShift(result.getCustomShiftName()).build();
                    if (Objects.isNull(result.getSignInTime())) {
                        detailResult.setCheckInTimes(result.getCheckinTimes() == null ? "" : result.getCheckinTimes().toString());
                    }
                    detailResultList.add(detailResult);
                }
            }
        });
        List<AttendanceDetailResult> resultList = detailResultList.stream().sorted((o1, o2) -> {
            if (!o1.getDayBeginTime().equals(o2.getDayBeginTime())) {
                return o1.getDayBeginTime().compareTo(o2.getDayBeginTime());
            } else if (!o1.getDeptName().equals(o2.getDeptName())) {
                return o1.getDeptName().compareTo(o2.getDeptName());
            } else if (!o1.getName().equals(o2.getName())) {
                return o1.getName().compareTo(o2.getName());
            } else if (StrUtil.isNotEmpty(o1.getGongHao()) && StrUtil.isNotEmpty(o2.getGongHao()) && !o1.getGongHao().equals(o2.getGongHao())) {
                return o1.getGongHao().compareTo(o2.getGongHao());
            } else if (!o1.getCheckinTimeL().equals(o2.getCheckinTimeL())) {
                return o1.getCheckinTimeL().compareTo(o2.getCheckinTimeL());
            } else {
                return 0;
            }
        }).collect(Collectors.toList());

        int number = 1;
        for (AttendanceDetailResult result : resultList) {
            result.setNumber(number);
            number++;
        }
        return resultList;
    }

    public ApprovalExtraWork getApprovalExtraWork(List<ApprovalExtraWork> approvalExtraWorksOfAll, Periods todayApproval, WxCpUser user) {
        Optional<ApprovalExtraWork> find = approvalExtraWorksOfAll.stream().filter(a -> {
            if (a.getUserId().id().equals(user.getUserId())) {
                Periods approval = new Periods(a.getStartTime().getTime() / 1000, a.getEndTime().getTime() / 1000);
                return todayApproval.intersect(approval).sum() > 0;
            }
            return false;
        }).findFirst();
        return find.orElse(null);
    }

    public XSSFWorkbook makeWorkTimeSheet(XSSFWorkbook wb, Date start, Date end, CpId cpId, List<AttendanceResult> resultList, boolean allDept) {
        Sheet sheet = wb.createSheet("工时");
        // 内容
        List<WxCpUser> wxCpUsers = externalWxUserService.listAllUser(cpId);
        wxCpUsers = wxCpUsers.stream().sorted(Comparator.comparing(o -> o.getDepartIds()[0])).toList();

        // 数据获取范围为 开始时间0点 到 结束时间第二天末
        Date beginOfEndDay = DateUtil.beginOfDay(end);
        Date endOfEndDay = DateUtil.endOfDay(end);
        Date beginOfEndDayNext = DateUtil.offset(beginOfEndDay, DateField.DAY_OF_MONTH, 1);
        Date endOfEndDayNext = DateUtil.offset(endOfEndDay, DateField.DAY_OF_MONTH, 1);
        Periods dayShiftPeriod = new Periods(beginOfEndDay.getTime() / 1000, endOfEndDay.getTime() / 1000);
        Periods nightShiftPeriod = new Periods(beginOfEndDayNext.getTime() / 1000, endOfEndDayNext.getTime() / 1000);
        // 需要查询加班的用户ID
        List<String> userIds = wxCpUsers.stream().map(WxCpUser::getUserId).toList();
        PassedApprovalQuery query = PassedApprovalQuery.builder().cpId(cpId).userIds(userIds).start(beginOfEndDay).end(endOfEndDayNext).build();
        List<ApprovalExtraWork> approvalExtraWorksOfAll = approvalRepository.listPassedApprovalAttendance(query);

        List<Long> daysBeginTimeList = attendanceHelper.getBeginTimeStampOfDays(start, end);
        int dayNum = daysBeginTimeList.size();
        // 第一行 标题
        Row titleRow = sheet.createRow(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        titleRow.createCell(0).setCellValue(sdf.format(start) + "-" + sdf.format(end));

        // 第二行 表头
        Row row1 = sheet.createRow(1);
        Row row2 = sheet.createRow(2);
        // 序号
        row1.createCell(0).setCellValue("序号");
        CellRangeAddress numberRegion = new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(numberRegion);
        // 部门
        row1.createCell(1).setCellValue("部门");
        CellRangeAddress deptRegion = new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(deptRegion);
        // 姓名
        row1.createCell(2).setCellValue("姓名");
        CellRangeAddress nameRegion = new CellRangeAddress(1, 2, 2, 2);
        sheet.addMergedRegion(nameRegion);
        // 工号
        row1.createCell(3).setCellValue("工号");
        CellRangeAddress gongHaoRegion = new CellRangeAddress(1, 2, 3, 3);
        sheet.addMergedRegion(gongHaoRegion);
        Calendar c = Calendar.getInstance();
        // 合计
        row1.createCell(dayNum + 4).setCellValue("合计");
        CellRangeAddress totalRegion = new CellRangeAddress(1, 2, dayNum + 4, dayNum + 4);
        sheet.addMergedRegion(totalRegion);
        // 加班理由
        row1.createCell(dayNum + 5).setCellValue("加班信息（" + sdf.format(end) + ")");
        CellRangeAddress reasonRegion = new CellRangeAddress(1, 2, dayNum + 5, dayNum + 5);
        sheet.addMergedRegion(reasonRegion);
        // 日期和星期
        int dayNumber = 0;
        for (Long dayStartTime : daysBeginTimeList) {
            c.setTime(new Date(dayStartTime * 1000));
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Cell cayOfWeekCell = row1.createCell(dayNumber + 4);
            cayOfWeekCell.setCellValue(attendanceHelper.getChineseDayOfWeek(dayOfWeek));
            Cell cayOfMonthCell = row2.createCell(dayNumber + 4);
            cayOfMonthCell.setCellValue(dayOfMonth);
            dayNumber++;
        }

        int rowNumber = 3;
        for (WxCpUser user : wxCpUsers) {
            // 获取最后一天的加班信息
            ApprovalExtraWork lastDayApproval = null;
            WxCpDepart wxCpDepart = externalWxDeptService.getDept(cpId, new WxDeptId(user.getDepartIds()[0]));
            WxDept dept = attendanceAssembler.toWxDept(wxCpDepart);
            attendanceHelper.setDeptFullName(dept, cpId, wxCpDepart);
            if (!allDept && cpId.id().equals(WxCp.AUTOMATION.getCpId())) {
                // 只导出生产部门的
                boolean prodDept = attendanceHelper.isProdDept(dept.getFullName());
                if (!prodDept) {
                    continue;
                }
            }
            double totalWorkTime = 0;
            dayNumber = 0;
            Row dataRow = sheet.createRow(rowNumber);
            dataRow.createCell(0).setCellValue(rowNumber - 2);
            dataRow.createCell(1).setCellValue(dept.getFullName());
            dataRow.createCell(2).setCellValue(user.getName());
            dataRow.createCell(3).setCellValue(workWxHelper.getEmployeeNo(user));

            DecimalFormat df = new DecimalFormat("##0.0");
            for (Long dayTimeSec : daysBeginTimeList) {
                AttendanceResult result = resultList.stream().filter(r -> r.getDay().getTime() / 1000 == dayTimeSec && r.getUserId().id().equals(user.getUserId())).findFirst().orElse(null);
                if (result != null) {
                    Cell dataCell = dataRow.createCell(dayNumber + 4);
                    if (result.getWorkTime() != null) {
                        // 加班申请：有夜班和白班区分
                        if (dayTimeSec == end.getTime() / 1000) {
                            if (result.getShift().isDayShift()) {
                                lastDayApproval = getApprovalExtraWork(approvalExtraWorksOfAll, dayShiftPeriod, user);
                            } else {
                                lastDayApproval = getApprovalExtraWork(approvalExtraWorksOfAll, nightShiftPeriod, user);
                            }
                        }
                        double workTime = Double.valueOf(result.getWorkTime()) / 60 / 60;
                        String timeStr = df.format(workTime);
                        if (!"0.0".equals(timeStr)) {
                            dataCell.setCellValue(timeStr);
                            totalWorkTime += workTime;
                        }
                    }
                }
                dayNumber++;
            }
            dataRow.createCell(dayNumber + 4).setCellValue(df.format(totalWorkTime));
            dataRow.createCell(dayNumber + 5).setCellValue(lastDayApproval != null ? lastDayApproval.getReason() : "");
            rowNumber++;
        }
        // 边框
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        for (int i = 1; i < rowNumber; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < dayNum + 6; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                cell.setCellStyle(cellStyle);
            }
        }
        return wb;
    }
}
