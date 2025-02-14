package com.greenstone.mes.meal.application.helper;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.greenstone.mes.common.utils.CouponCodeUtil;
import com.greenstone.mes.meal.domain.entity.MealTicket;
import com.greenstone.mes.meal.infrastructure.constant.MealConst;
import com.greenstone.mes.office.meal.dto.cmd.MealReportCmd;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class MealHelper {

    /**
     * taskId：任务类型@业务数据@UUID
     */
    public String genTaskId(Integer mealType, Integer reportType, LocalDate date) {
        return StrUtil.format("meal_report@{}@{}@{}@{}", mealType, reportType, LocalDateTimeUtil.format(date, "yyyyMMdd"), IdUtil.fastUUID());
    }

    public void setMealReportTypeAndDate(MealReportCmd mealReportCmd, String taskId) {
        String[] strs = taskId.split("@");
        mealReportCmd.setMealType(Integer.parseInt(strs[1]));
        mealReportCmd.setReportType(Integer.parseInt(strs[2]));
        mealReportCmd.setDay(LocalDateTimeUtil.parseDate(strs[3], "yyyyMMdd"));
    }


    public String genTicketCode(long userId) {
        String uid = NumberUtil.decimalFormat("0000", userId);
        if (uid.length() > 4) {
            uid = uid.substring(uid.length() - 4);
        }
        return uid + DateUtil.format(new Date(), "yyDDD").substring(1, 5) + CouponCodeUtil.generateCouponCodeV2();
    }

    public BufferedImage generateTicketQrCode(String content, String remark) {
        BufferedImage bufferedImage = QrCodeUtil.generate(content, 200, 230);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.BLACK);
        Font font = new Font("宋体", Font.PLAIN, 16);
        FontMetrics metrics = graphics.getFontMetrics(font);
        // 添加券码
        String contentDisplay = displayContentFormat(content);
        int startX = (200 - metrics.stringWidth(contentDisplay)) / 2;
        int startY = 220;
        graphics.setFont(font);
        graphics.drawString(contentDisplay, startX, startY);
        // 添加说明
        int startX2 = (200 - metrics.stringWidth(remark)) / 2;
        int startY2 = 24;
        graphics.setFont(font);
        graphics.drawString(remark, startX2, startY2);
        graphics.dispose();
        return bufferedImage;
    }

    private String displayContentFormat(String content) {
        StringBuilder sb = new StringBuilder();
        int maxTimes = content.length() / 4 + 1;
        for (int i = 0; i < maxTimes; i++) {
            int endIndex = i * 4 + 4;
            sb.append(content, i * 4, Math.min(endIndex, content.length()));
            if (i < maxTimes - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public String getTicketRemark(MealTicket ticket, Integer num) {
        String remark = LocalDateTimeUtil.format(ticket.getDay(), "yyyy-MM-dd") + " "
                + LocalDateTimeUtil.dayOfWeek(ticket.getDay()).toChinese("周") + " "
                + (LocalDateTimeUtil.parseDate("20250111", "yyyyMMdd").equals(ticket.getDay()) ? "年夜饭" : (MealConst.MealType.LUNCH == ticket.getMealType() ? "午饭" : "晚饭"));
        if (num != null) {
            remark = remark + " #" + num;
        }
        return remark;
    }

    public Integer getMealTypeNow() {
        LocalDateTime middleOfToday = LocalDateTime.now().withHour(14).withMinute(0).withSecond(0);
        if (LocalDateTime.now().isBefore(middleOfToday)) {
            return MealConst.MealType.LUNCH;
        } else {
            return MealConst.MealType.DINNER;
        }
    }

    public String getMealTypeName() {
        return getMealTypeName(getMealTypeNow());
    }

    public String getMealTypeName(Integer mealType) {
        return MealConst.MealType.LUNCH == mealType ? "午餐" : "晚餐";
    }

    public String getMealReportTitle(Integer mealType) {
        String dateStr = LocalDateTimeUtil.format(LocalDate.now(), "yyyy-MM-dd");
        String dayOfWeek = LocalDateTimeUtil.dayOfWeek(LocalDate.now()).toChinese("周");
        String mealName = null;
        if (MealConst.MealType.LUNCH == mealType) {
            mealName = "午饭";
        } else if (MealConst.MealType.DINNER == mealType) {
            mealName = "晚饭";
        }
        return dateStr + " " + dayOfWeek + " " + mealName;
    }

    public String getMealReportTitleDesc(Integer mealType) {
        if (MealConst.MealType.LUNCH == mealType) {
            return "每日7:00-9:00，可在打卡后报餐";
        } else {
            return "每日7:00-14:00，可在申请加班后报餐";
        }
    }

}
