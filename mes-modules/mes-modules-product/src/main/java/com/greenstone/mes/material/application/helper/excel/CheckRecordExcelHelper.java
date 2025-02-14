package com.greenstone.mes.material.application.helper.excel;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.material.application.dto.ExcelPictureDto;
import com.greenstone.mes.material.domain.entity.CheckRecord;
import com.greenstone.mes.material.infrastructure.enums.CheckResult;
import com.greenstone.mes.material.infrastructure.enums.NgType;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.FileRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-12-22-14:21
 */
@Slf4j
@Service
public class CheckRecordExcelHelper {

    private final RemoteFileService fileService;

    public CheckRecordExcelHelper(RemoteFileService fileService) {
        this.fileService = fileService;
    }

    public XSSFWorkbook makeCheckRecordExcel(List<CheckRecord> checkRecords) {
        XSSFWorkbook wb = new XSSFWorkbook();
        // 超链接样式
        XSSFCellStyle linkStyle = wb.createCellStyle();
        XSSFFont cellFont = wb.createFont();
        cellFont.setUnderline((byte) 1);
        cellFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkStyle.setFont(cellFont);

        Sheet leaveSheet = wb.createSheet("检验结果");
        Sheet fileSheet = wb.createSheet("附件");

        Row titleRow = leaveSheet.createRow(0);
        titleRow.createCell(0).setCellValue("序号");
        titleRow.createCell(1).setCellValue("项目代码");
        titleRow.createCell(2).setCellValue("零件号");
        titleRow.createCell(3).setCellValue("零件版本");
        titleRow.createCell(4).setCellValue("零件名称");
        titleRow.createCell(5).setCellValue("结果");
        titleRow.createCell(6).setCellValue("数量");
        titleRow.createCell(7).setCellValue("NG大类");
        titleRow.createCell(8).setCellValue("NG小类");
        titleRow.createCell(9).setCellValue("检验人");
        titleRow.createCell(10).setCellValue("时间");
        titleRow.createCell(11).setCellValue("备注");
        titleRow.createCell(12).setCellValue("图片数量");

        int rowNum = 1;
        for (CheckRecord checkRecord : checkRecords) {
            Row dataRow = leaveSheet.createRow(rowNum);
            dataRow.createCell(0).setCellValue(rowNum);
            dataRow.createCell(1).setCellValue(checkRecord.getProjectCode());
            dataRow.createCell(2).setCellValue(checkRecord.getMaterialCode());
            dataRow.createCell(3).setCellValue(checkRecord.getMaterialVersion());
            dataRow.createCell(4).setCellValue(checkRecord.getMaterialName());
            dataRow.createCell(5).setCellValue(CheckResult.getById(checkRecord.getResult()) == null ? "" : CheckResult.getById(checkRecord.getResult()).getName());
            dataRow.createCell(6).setCellValue(checkRecord.getNumber());
            dataRow.createCell(7).setCellValue(NgType.getByType(checkRecord.getNgType()) == null ? "" : NgType.getByType(checkRecord.getNgType()).getName());
            dataRow.createCell(8).setCellValue(NgType.getByType(checkRecord.getSubNgType()) == null ? "" : NgType.getByType(checkRecord.getSubNgType()).getName());
            dataRow.createCell(9).setCellValue(checkRecord.getSponsor());
            dataRow.createCell(10).setCellValue(checkRecord.getTime() == null ? "" : checkRecord.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dataRow.createCell(11).setCellValue(checkRecord.getRemark());
            // 附件
            R<List<FileRecord>> info = fileService.info(checkRecord.getId(), 3);
            if (info.isPresent()) {
                List<FileRecord> fileRecordList = info.getData();
                Cell fileNumCell = dataRow.createCell(12);
                int imageNum = checkRecord.getImageNum() == null ? 0 : checkRecord.getImageNum();
                int imageFound = 0;
                int fileCellNum = 0;
                // 固定列宽20厘米
                double cellWidth = 20;
                // 固定行高10厘米
                double cellHeight = 10;
                // 单元格宽高比
                double cellRate = cellWidth / cellHeight;
                List<ExcelPictureDto> excelPictureDtoList = new ArrayList<>();
                if (CollUtil.isNotEmpty(fileRecordList)) {
                    for (FileRecord fileRecord : fileRecordList) {
                        try {
                            File file = new File(fileRecord.getLocalFilePath());

                            try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
                                // 将图片写入输出流
                                BufferedImage bufferImg = ImageIO.read(file);
                                // 图片格式
                                String formatName = getPictureFormat(file);
                                // 写入图片到表格
                                ImageIO.write(bufferImg, formatName, byteArrayOut);

                                excelPictureDtoList.add(ExcelPictureDto.builder().name(FilenameUtils.getName(fileRecord.getName()))
                                        .bufferImg(bufferImg).byteArrayOut(byteArrayOut).height(bufferImg.getHeight()).width(bufferImg.getWidth()).type(formatName).build());
                                imageFound++;
                            }

                        } catch (ServiceException | IOException e) {
                            log.error("Get file from check record error", e);
                        }
                    }
                }
                // 设置单元格内容
                if (imageFound < imageNum) {
                    fileNumCell.setCellValue(imageNum + "个(其中" + (imageNum - imageFound) + "张图片未找到)");
                } else {
                    fileNumCell.setCellValue(imageNum + "个");
                }
                // 设置单元格超链接及图片展示格式调整
                for (ExcelPictureDto excelPictureDto : excelPictureDtoList) {
                    Row fileRow = fileSheet.createRow(rowNum);
                    fileRow.createCell(fileCellNum);
                    // 设置宽（wps单元格默认列宽单位为字符，1厘米=5.21字符，257.67为单元格宽度（字符）和这里设置的的值的倍数）
                    fileSheet.setColumnWidth(fileCellNum, (int) (cellWidth * 5.21 * 257.67));
                    // 设置高（wps单元格默认行高单位为磅，1厘米=28.34磅，20为单元格高度（磅）和这里设置的值的倍数）
                    fileRow.setHeight((short) (cellHeight * 28.34 * 20));
                    // 设置图片超链接
                    XSSFCreationHelper creationHelper = wb.getCreationHelper();
                    XSSFHyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
                    String address = "#附件!A" + (rowNum + 1);
                    hyperlink.setAddress(address);
                    fileNumCell.setHyperlink(hyperlink);
                    // 设置样式
                    fileNumCell.setCellStyle(linkStyle);

                    // 利用HSSFPatriarch将图片写入EXCEL
                    Drawing<?> patriarch = fileSheet.createDrawingPatriarch();
                    // 图片宽高比
                    double rate = (double) excelPictureDto.getWidth() / (double) excelPictureDto.getHeight();
                    // x轴缩进
                    double dx = 0;
                    // y轴缩进
                    double dy = 0;
                    // 图片宽高比大于单元格宽高比
                    if (rate >= cellRate) {
                        // y轴缩进
                        dy = cellHeight - cellWidth / rate;
                    } else {
                        // x轴缩进
                        dx = cellWidth - rate * cellHeight;
                    }
                    // 图片放入的单元格范围：dxy:内容在单元格内坐标，c1r1:放入单元格的左上角坐标，c2r2:右下角坐标
                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, Units.EMU_PER_CENTIMETER * (-(int) dx),
                            Units.EMU_PER_CENTIMETER * (-(int) dy), fileCellNum, rowNum, fileCellNum + 1, rowNum + 1);
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                    // 插入图片内容
                    patriarch.createPicture(anchor, wb.addPicture(excelPictureDto.getByteArrayOut()
                            .toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG));
                    fileCellNum++;
                }
            }
            rowNum++;
        }
        return wb;
    }

    /**
     * 获取图片（真实）格式
     */
    public String getPictureFormat(File file) {
        String pictureFormat = "";
        try {
            if (file.exists()) {
                // 图片实际格式
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
                Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
                if (!imageReaders.hasNext()) {
                    return "jpeg";
                }
                ImageReader reader = imageReaders.next();
                imageInputStream.close();
                pictureFormat = reader.getFormatName();
            }
        } catch (IOException e) {
            log.error("Get file format error", e);
        }

        return pictureFormat;
    }

}
