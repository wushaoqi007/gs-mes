package com.greenstone.mes.material.application.helper;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.material.constant.MaterialConst;
import com.greenstone.mes.material.domain.BaseWarehouse;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class WarehouseHelper {

    private final RemoteFileService fileService;

    public SysFile genQrcodePdf(List<BufferedImage> images, int x, int y) {
        try {
            int imgWidth = x * 3;
            int imgHeight = y * 3;
            int pdfWidth = 612;
            int pdfHeight = 792;
            int horizontalNum = Math.min(pdfWidth / imgWidth, 3);
            int verticalNum = Math.min(pdfHeight / imgHeight, 3);
            int numPerPage = horizontalNum * verticalNum;
            float intervalX = (pdfWidth - imgWidth * horizontalNum) / (horizontalNum + 1f);
            float intervalY = (pdfHeight - imgHeight * verticalNum) / (verticalNum + 1f);
            PDDocument document = new PDDocument();
            int pageIndex;
            for (int i = 0; i < images.size(); i++) {
                pageIndex = i / numPerPage;
                if (i % numPerPage == 0) {
                    PDPage pdPage = new PDPage();
                    document.addPage(pdPage);
                    PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, PDPageContentStream.AppendMode.PREPEND, false);
                    // 画分割线
                    drawDottedLine(contentStream, pdfWidth, pdfHeight, imgWidth, imgHeight, intervalX, intervalY, horizontalNum, verticalNum);
                    // 页号
                    String pageNum = String.valueOf(pageIndex + 1);
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                    contentStream.beginText();
                    float stringWidth = PDType1Font.TIMES_ROMAN.getStringWidth(pageNum) / 1000;
                    contentStream.newLineAtOffset((pdfWidth - stringWidth) / 2 - 5f, 10);
                    contentStream.showText(pageNum);
                    contentStream.endText();

                    contentStream.close();
                }
                PDPage page = document.getPage(pageIndex);
                PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.PREPEND, false);

                BufferedImage image = images.get(i);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageInByte, String.valueOf(i));
                int line = i % numPerPage / horizontalNum + 1;
                int column = i % horizontalNum + 1;
                contentStream.drawImage(pdImage, intervalX * (column) + imgWidth * (column - 1),
                        pdfHeight - (imgHeight + intervalY) * line, imgWidth, imgHeight);

                contentStream.close();
            }
            // 将pdf转为输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();
            // 将输出流转为 multipartFile 并上传
            MockMultipartFile multipartFile = new MockMultipartFile("file", "工卡导出.pdf", null, baos.toByteArray());
            baos.close();
            R<SysFile> upload = fileService.upload(multipartFile, 1);
            return upload.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 画 pdf 中的分割线
     */
    private void drawDottedLine(PDPageContentStream contentStream, int pdfWidth, int pdfHeight, int imgWidth, int imgHeight, float intervalX, float intervalY, int horizontalNum, int verticalNum) throws IOException {
        contentStream.setLineDashPattern(new float[]{3}, 0);
        contentStream.setLineWidth(0.2f);
        // 关于 integer division in floating-point context 的警告不要修改
        for (int i = 0; i < horizontalNum * 2; i++) {
            float x = (intervalX * (i / 2 + 1)) + imgWidth * ((i + 1) / 2);
            contentStream.moveTo(x, 0);
            contentStream.lineTo(x, pdfHeight);
            contentStream.stroke();
        }
        for (int i = 0; i < verticalNum * 2; i++) {
            float y = (intervalY * (i / 2 + 1)) + imgHeight * ((i + 1) / 2);
            contentStream.moveTo(0, y);
            contentStream.lineTo(pdfWidth, y);
            contentStream.stroke();
        }
    }

    public List<BufferedImage> getQrCodeImages(List<BaseWarehouse> warehouses) {
        List<BufferedImage> images = new ArrayList<>();
        for (BaseWarehouse warehouse : warehouses) {
            images.add(genWhQrcode(MaterialConst.WAREHOUSE_PREFIX + warehouse.getCode(), warehouse.getCode()));
        }
        return images;
    }

    private BufferedImage genWhQrcode(String qrCode, String whCode) {
        // 创建图片对象
        int rate = 2;
        int width = 200 * rate;
        int height = 225 * rate;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        // 背景颜色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        // 添加二维码图片
        BufferedImage qrCodeImage = QrCodeUtil.generate(qrCode, width, height);
        graphics.drawImage(qrCodeImage, 0, -30, null, null);
        // 设置字体
        graphics.setColor(Color.BLACK);
        Font font = new Font("微软雅黑", Font.BOLD, 20 * rate);
        FontMetrics metrics = graphics.getFontMetrics(font);
        // 添加仓库名称
        int startX = (width - metrics.stringWidth(whCode)) / 2;
        int startY = width + (height - width) / 2;
        graphics.setFont(font);
        graphics.drawString(whCode, startX, startY);
        graphics.dispose();
        return image;
    }

    public static void main(String[] args) throws IOException {
        String qrCode = "BC0001";
        String whName = "一次表处区";
        int rate = 2;
        int width = 200 * rate;
        int height = 225 * rate;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        // 背景颜色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        // 添加二维码图片
        BufferedImage qrCodeImage = QrCodeUtil.generate(qrCode, width, height);
        graphics.drawImage(qrCodeImage, 0, -20, null, null);
        // 设置字体
        graphics.setColor(Color.BLACK);
        Font font = new Font("微软雅黑", Font.BOLD, 20 * rate);
        FontMetrics metrics = graphics.getFontMetrics(font);
        // 添加仓库名称
        int startX = (width - metrics.stringWidth(whName)) / 2;
        int startY = width + (height - width) / 2 + 5;
        graphics.setFont(font);
        graphics.drawString(whName, startX, startY);
        graphics.dispose();
        File outputfile = new File("D:\\image.png");
        ImageIO.write(image, "png", outputfile);
    }

}
