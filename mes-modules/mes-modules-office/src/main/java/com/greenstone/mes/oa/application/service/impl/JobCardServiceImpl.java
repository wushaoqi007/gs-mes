package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.oa.application.service.JobCardService;
import com.greenstone.mes.oa.dto.cmd.JobCardPrintCmd;
import com.greenstone.mes.system.api.RemoteDeptService;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.dto.query.UserQuery;
import com.greenstone.mes.system.dto.result.UserResult;
import lombok.*;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class JobCardServiceImpl implements JobCardService {

    private final RemoteUserService userService;
    private final RemoteDeptService deptService;
    private final RemoteFileService fileService;

    @Override
    public SysFile genStationPdf(JobCardPrintCmd printCmd) {
        List<UserResult> userResults = userService.gsUserBriefs(UserQuery.builder().userIds(printCmd.getUserIds()).build());
        setDeptName(userResults);
        return genStationPdf(userResults);
    }

    @Override
    public SysFile jobCardPdf(JobCardPrintCmd printCmd) {
        List<UserResult> userResults = userService.gsUserBriefs(UserQuery.builder().userIds(printCmd.getUserIds()).build());
        setDeptName(userResults);
        return genCardPdf(userResults);
    }

    private void setDeptName(List<UserResult> userResults) {
        for (UserResult user : userResults) {
            SysDept dept = deptService.getSysDept(SysDept.builder().deptId(user.getDeptId()).build());
            if (dept != null) {
                String[] deptIds = dept.getAncestors().split(",");
                if (deptIds.length > 2) {
                    dept = deptService.getSysDept(SysDept.builder().deptId(Long.valueOf(deptIds[2])).build());
                }
            } else {
                dept = SysDept.builder().deptName("所属部门不存在").build();
            }
            user.setDeptName(dept.getDeptName());
        }
    }


    private SysFile genCardPdf(List<UserResult> userResults) {
        if (CollUtil.isEmpty(userResults)) {
            throw new RuntimeException("请选择至少一条数据。");
        }
        List<JobCard> jobCards = new ArrayList<>();
        for (UserResult user : userResults) {
            JobCard jobCard = JobCard.builder().nickName(user.getNickName()).empNo(user.getEmployeeNo()).deptName(user.getDeptName()).build();
            jobCards.add(jobCard);
        }
        List<BufferedImage> images = genCardImg(jobCards);
        return insertPng2Pdf(images, 84, 53);
    }

    private SysFile genStationPdf(List<UserResult> userResults) {
        if (CollUtil.isEmpty(userResults)) {
            throw new RuntimeException("请选择至少一条数据。");
        }
        List<JobCard> jobCards = new ArrayList<>();
        for (UserResult user : userResults) {
            JobCard jobCard = JobCard.builder().nickName(user.getNickName()).empNo(user.getEmployeeNo()).deptName(user.getDeptName()).build();
            jobCards.add(jobCard);
        }
        List<BufferedImage> images = genCardImg(jobCards);
        return insertPng2Pdf(images, 148, 101);
    }

    private List<BufferedImage> genCardImg(List<JobCard> jobCards) {
        List<BufferedImage> images = new ArrayList<>();
        for (JobCard jobCard : jobCards) {
            String name = jobCard.getNickName();
            String empNo = jobCard.getEmpNo();
            String deptName = jobCard.getDeptName();
            BufferedImage image = generateJobCardImage(name, empNo, deptName);
            images.add(image);
        }
        return images;
    }

    private BufferedImage generateJobCardImage(String userName, String empNo, String deptName) {
        int rate = 6;
        int width = 254 * rate;
        int height = 163 * rate;
        // 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 背景颜色
        Graphics2D background = image.createGraphics();
        background.setColor(Color.WHITE);
        background.fillRect(0, 0, width, height);
        Color green = new Color(34, 166, 66);

        // --- 公司名称
        Graphics2D company = image.createGraphics();
        int companyHeight = 42 * rate;
        // 绿色条纹
        company.setColor(green);
        company.fillRect(0, 0, width, companyHeight);
        company.setColor(Color.WHITE);
        company.fillRect(0, 38 * rate, width, 2 * rate);
        // 公司名称
        String chineseName = "格林司通";
        company.setColor(Color.white);
        Font companyFont = new Font("微软雅黑", Font.BOLD, 18 * rate);
        company.setFont(companyFont);
        FontMetrics metrics = company.getFontMetrics(companyFont);
        company.drawString(chineseName, (width - metrics.stringWidth(chineseName)) / 2, 21 * rate);
        // 英文名称
        String engName = "GREENSTONE AUTOMATION";
        company.setColor(Color.white);
        Font companyEngFont = new Font("微软雅黑", Font.PLAIN, 8 * rate);
        company.setFont(companyEngFont);
        FontMetrics engMetrics = company.getFontMetrics(companyEngFont);
        company.drawString(engName, (width - engMetrics.stringWidth(engName)) / 2, 33 * rate);
        company.dispose();

        // --- 底部
        Graphics2D bottom = image.createGraphics();
        int bottomHeight1 = 8 * rate;
        int bottomHeight2 = 1 * rate;
        int bottomInterval = 2 * rate;
        int bottomHeight = bottomHeight1 + bottomInterval + bottomHeight2;
        bottom.setColor(green);
        bottom.fillRect(0, height - bottomHeight1, width, bottomHeight1);
        bottom.fillRect(0, height - bottomHeight, width, bottomHeight2);
        bottom.dispose();

        // 二维码
        String qrCode = StrUtil.format("""
                姓名：{}
                工号：{}
                部门：{}""", userName, empNo, deptName);
        int qrCodeWight = 88 * rate;
        int qrCodeHeight = 88 * rate;
        Graphics2D qrCodeImage = image.createGraphics();
        BufferedImage qrCodeImage2 = QrCodeUtil.generate(qrCode, qrCodeWight, qrCodeHeight);
        int qrCOdeY = companyHeight + (height - companyHeight - bottomHeight - qrCodeHeight) / 2;
        qrCodeImage.drawImage(qrCodeImage2, 15 * rate, qrCOdeY, qrCodeWight, qrCodeHeight, null);
        qrCodeImage.dispose();

        // 姓名
        Graphics2D nameImage = image.createGraphics();
        Font nameFont = new Font("微软雅黑", Font.BOLD, 22 * rate);
        nameImage.setFont(nameFont);
        FontMetrics nameMetrics = company.getFontMetrics(nameFont);
        nameImage.setColor(Color.BLACK);
        int nameX = qrCodeWight + (width - qrCodeWight - nameMetrics.stringWidth(userName)) / 2;
        int nameY = companyHeight + 35 * rate;
        nameImage.drawString(userName, nameX, nameY);
        nameImage.dispose();

        // 工号
        if (StrUtil.isNotBlank(empNo)) {
            Graphics2D noImage = image.createGraphics();
            Font noFont = new Font("微软雅黑", Font.BOLD, 14 * rate);
            noImage.setFont(noFont);
            FontMetrics noMetrics = company.getFontMetrics(noFont);
            noImage.setColor(Color.BLACK);
            int noX = qrCodeWight + (width - qrCodeWight - noMetrics.stringWidth(empNo)) / 2;
            int noY = companyHeight + 63 * rate;
            noImage.drawString(empNo, noX, noY);
            noImage.dispose();
        }

        // 部门
        if (StrUtil.isNotBlank(deptName)) {
            Graphics2D deptImage = image.createGraphics();
            Font deptFont = new Font("微软雅黑", Font.BOLD, 18 * rate);
            deptImage.setFont(deptFont);
            FontMetrics deptMetrics = company.getFontMetrics(deptFont);
            deptImage.setColor(Color.BLACK);
            int deptX = qrCodeWight + (width - qrCodeWight - deptMetrics.stringWidth(deptName)) / 2;
            int deptY = companyHeight + 93 * rate;
            deptImage.drawString(deptName, deptX, deptY);
            deptImage.dispose();
        }

        return image;
    }

    /**
     * 工卡 84 53
     * 工位牌 148 101
     * 大概 3 宽度对应 1 毫米
     *
     * @param images 需要打印的图片
     * @param x      图片实际宽度，毫米
     * @param y      图片实际高度，毫米
     * @return 生成文件的链接
     */
    private SysFile insertPng2Pdf(List<BufferedImage> images, int x, int y) {
        try {
            int imgWidth = x * 3;
            int imgHeight = y * 3;
            int pdfWidth = 612;
            int pdfHeight = 792;
            int horizontalNum = pdfWidth / imgWidth;
            int verticalNum = pdfHeight / imgHeight;
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
                if (i % horizontalNum == 0) {
                    contentStream.drawImage(pdImage, intervalX, pdfHeight - (imgHeight + intervalY) * line, imgWidth, imgHeight);
                } else {
                    contentStream.drawImage(pdImage, intervalX + imgWidth + intervalX, pdfHeight - (imgHeight + intervalY) * line, imgWidth, imgHeight);
                }
                contentStream.close();
            }
            // document.save("D:\\b.pdf");
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class JobCard {
        private String nickName;

        private String empNo;

        private String deptName;
    }

}
