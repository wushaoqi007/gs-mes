package com.greenstone.mes.oa.application.helper.excel;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.application.dto.attendance.result.LeaveApprovalExport;
import com.greenstone.mes.wxcp.domain.helper.WxMediaService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.FileId;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ApprovalExcelHelper {

    @Autowired
    private WxMediaService externalWxMediaService;

    public XSSFWorkbook getLeaveApprovalExcel(List<LeaveApprovalExport> exportList, String cpId) {

        XSSFWorkbook wb = new XSSFWorkbook();
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
        for (LeaveApprovalExport approvalExport : exportList) {
            Row dataRow = leaveSheet.createRow(rowNum);
            dataRow.createCell(0).setCellValue(rowNum);
            dataRow.createCell(1).setCellValue(approvalExport.getName());
            dataRow.createCell(2).setCellValue(approvalExport.getType());
            dataRow.createCell(3).setCellValue(approvalExport.getStartTime());
            dataRow.createCell(4).setCellValue(approvalExport.getEndTime());
            dataRow.createCell(5).setCellValue(approvalExport.getReason());
            dataRow.createCell(6).setCellValue(approvalExport.getApprover());
            if (CollUtil.isNotEmpty(approvalExport.getFileIds())) {
                Cell fileNumCell = dataRow.createCell(7);
                fileNumCell.setCellValue(approvalExport.getFileIds().size() + "个");

                for (String fileId : approvalExport.getFileIds()) {
                    try {
                        File file = externalWxMediaService.download(new CpId(cpId), new FileId(fileId));
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

}
