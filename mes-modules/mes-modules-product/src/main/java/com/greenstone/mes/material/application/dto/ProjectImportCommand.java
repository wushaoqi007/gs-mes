package com.greenstone.mes.material.application.dto;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.BizError;
import com.greenstone.mes.common.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DateUtil;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ProjectImportCommand {

    @Valid
    @NotEmpty
    private List<ProjectInfo> projects;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectInfo {
        @NotEmpty(message = "产品代码不能为空")
        private String projectCode;

        @NotEmpty(message = "客户名称不能为空")
        private String customerName;
        @NotEmpty(message = "客户简称不能为空")
        private String customerShortName;

        @NotEmpty(message = "立项日期不能为空")
        private String projectInitiationTime;

        private String gsOrganization;

        private String productionType;

        @NotEmpty(message = "产品名称不能为空")
        private String projectName;

        @NotNull(message = "数量不能为空")
        private Integer number;

        private String unit;

        private String designDeadline;

        @NotEmpty(message = "客户纳期不能为空")
        private String customerDeadline;

        private String orderCode;

        private String orderReceiveTime;

        private String customerDirector;

        private String designerDirector;

        private String electricalDirector;

        private Boolean softwareJoin;

        private String softwareDirector;

        private String businessDirector;

        private String remark;

        private String firstQuotation;

        private String lastQuotation;

        private String sameOrder;

        public void trim() {
            List<String> trims = List.of(" ");
            projectCode = StrUtil.trim(projectCode, 0, character -> trims.contains(String.valueOf(character)));
            projectCode = projectCode.replaceAll("\r", "");
            projectCode = projectCode.replaceAll("\n", "");
            projectName = StrUtil.trim(projectName, 0, character -> trims.contains(String.valueOf(character)));
            projectName = projectName.replaceAll("\r", "");
            projectName = projectName.replaceAll("\n", "");
            orderCode = StrUtil.trim(orderCode, 0, character -> trims.contains(String.valueOf(character)));
            orderCode = orderCode.replaceAll("\r", "");
            orderCode = orderCode.replaceAll("\n", "");
        }

        public Date validateAndFormatProjectInitiationTime(String timeStr) {
            if (StrUtil.isEmpty(timeStr)) {
                return null;
            } else {
                try {
                    if (timeStr.contains("CST")) {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        return format.parse(timeStr);
                    }
                    return DateUtil.getJavaDate(Double.parseDouble(timeStr));
                } catch (Exception e) {
                    log.error("projectInitiationTime format error:{}", timeStr);
                    throw new ServiceException(BizError.E40001, StrUtil.format("错误位置：产品代码为{}的立项日期不正确", projectCode));
                }
            }
        }

        public Date validateAndFormatDesignDeadline(String timeStr) {
            if (StrUtil.isEmpty(timeStr)) {
                return null;
            } else {
                try {
                    if (timeStr.contains("CST")) {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        return format.parse(timeStr);
                    }
                    return DateUtil.getJavaDate(Double.parseDouble(timeStr));
                } catch (Exception e) {
                    log.error("designDeadline format error:{}", timeStr);
                    throw new ServiceException(BizError.E40001, StrUtil.format("错误位置：产品代码为{}的设计纳期不正确", projectCode));
                }
            }
        }

        public Date validateAndFormatOrderReceiveTime(String timeStr) {
            if (StrUtil.isEmpty(timeStr)) {
                return null;
            } else {
                try {
                    if (timeStr.contains("CST")) {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        return format.parse(timeStr);
                    }
                    return DateUtil.getJavaDate(Double.parseDouble(timeStr));
                } catch (Exception e) {
                    log.error("orderReceiveTime format error:{}", timeStr);
                    throw new ServiceException(BizError.E40001, StrUtil.format("错误位置：产品代码为{}的订单接收日不正确", projectCode));
                }
            }
        }

        public Date validateAndFormatCustomerDeadline(String timeStr) {
            if (StrUtil.isEmpty(timeStr)) {
                return null;
            } else {
                try {
                    if (timeStr.contains("CST")) {
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        return format.parse(timeStr);
                    }
                    return DateUtil.getJavaDate(Double.parseDouble(timeStr));
                } catch (Exception e) {
                    log.error("customerDeadline format error:{}", timeStr);
                    throw new ServiceException(BizError.E40001, StrUtil.format("错误位置：产品代码为{}的客户纳期不正确", projectCode));
                }
            }
        }
    }

    public void trim() {
        for (ProjectInfo project : projects) {
            project.trim();
        }
    }

}
