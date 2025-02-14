package com.greenstone.mes.oa.application.helper;

import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.common.core.enums.SysError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.oa.domain.entity.*;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.enums.ContentType;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;
import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentValue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author gu_renkai
 * @date 2022/11/17 13:59
 */
public class ApprovalAssemblerHelper {

    public static <T> T getContent(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type) {
        return getContent(approvalDetail, type, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContent(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return switch (type.getControl()) {
            case Date -> (T) controlToDate(approvalDetail, type, title);
            case Text -> (T) controlToText(approvalDetail, type, title);
            case Contact -> (T) controlToContact(approvalDetail, type, title);
            case Textarea -> (T) controlToTextarea(approvalDetail, type, title);
            case File -> (T) controlToFile(approvalDetail, type, title);
            case Vacation -> (T) controlToVacation(approvalDetail, type, title);
            case PunchCorrection -> (T) controlToApprovalPunch(approvalDetail, type, title);
            case Attendance -> (T) controlToAttendance(approvalDetail, type, title);
            default -> throw new ServiceException(SysError.E12001, type.getControl().getName());
        };
    }

    public static List<ApprovalComment> getComments(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        return approvalDetail.getComments().stream().map(c -> {
            List<WxMediaId> wxMediaIds = c.getMediaIds().stream().map(WxMediaId::new).toList();
            return ApprovalComment.builder().wxUserId(new WxUserId(c.getCommentUserInfo()
                    .getUserId())).content(c.getCommentContent()).mediaIds(wxMediaIds).build();
        }).toList();
    }

    private static <T> Date controlToDate(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> c.getValue().getDate().getTimestamp())
                .map(s -> new Date(Long.parseLong(s) * 1000))
                .orElse(null);
    }

    private static <T> ApprovalMembers controlToContact(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> c.getValue().getMembers())
                .map(m -> ApprovalMembers.builder().members(m).build())
                .orElse(null);
    }

    private static <T> String controlToText(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> c.getValue().getText())
                .orElse(null);
    }

    private static <T> String controlToTextarea(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> c.getValue().getText())
                .orElse(null);
    }

    private static <T> ApprovalContentFile controlToFile(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> c.getValue().getFiles().stream().map(f -> new WxMediaId(f.getFileId())).toList())
                .map(fids -> ApprovalContentFile.builder().fileIds(fids).build())
                .orElse(null);
    }

    private static <T> ApprovalContentVacation controlToVacation(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        // 找到名为 Vacation 的内容
        Optional<ApplyDataContent> contentOptional = approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst();
        if (contentOptional.isEmpty()) {
            return null;
        }
        Optional<ContentValue.Vacation> vacationOptional = contentOptional.map(c -> c.getValue().getVacation());
        // 组装请假类型数据
        Optional<ContentValue.Selector.Option> optionOptional = vacationOptional.map(v -> v.getSelector().getOptions().get(0));
        String key = optionOptional.map(ContentValue.Selector.Option::getKey).orElse(null);
        String text = optionOptional.map(o -> o.getValues().get(0).getText()).orElse(null);
        ApprovalContentVacation.Type vType = ApprovalContentVacation.Type.builder().key(key == null ? null : Integer.valueOf(key)).name(text).build();
        // 组装请假时间数据
        Optional<ContentValue.Attendance> attendance = vacationOptional.map(ContentValue.Vacation::getAttendance);
        ApprovalContentVacation.DateRange dateRange = attendance.map(a -> ApprovalContentVacation.DateRange.builder().begin(new Date(a.getDateRange().getBegin() * 1000)).end(new Date(a.getDateRange().getEnd() * 1000)).build()).orElse(null);

        return ApprovalContentVacation.builder().type(vType).dateRange(dateRange).build();
    }

    private static <T> ApprovalContentAttendance controlToAttendance(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        // 找到名为 Attendance 的内容
        Optional<ApplyDataContent> contentOptional = approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst();
        return contentOptional.map(c -> c.getValue().getAttendance())
                .map(a -> {
                    ApprovalContentAttendance.DateRange dateRange = ApprovalContentAttendance.DateRange.builder().type(a.getDateRange().getType())
                            .begin(new Date(a.getDateRange().getBegin() * 1000))
                            .end(new Date(a.getDateRange().getEnd() * 1000)).build();
                    return ApprovalContentAttendance.builder().dateRange(dateRange).build();

                }).orElse(null);
    }

    private static <T> ApprovalContentPunchCorrection controlToApprovalPunch(WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail, ContentType<T> type, String title) {
        return approvalDetail.getApplyData().getContents().stream()
                .filter(c -> c.getControl().equals(type.getControl().getName()) && (StrUtil.isEmpty(title) || title.equals(c.getTitles().get(0).getText())))
                .findFirst()
                .map(c -> {
                    ContentValue.PunchCorrection punchCorrection = c.getValue().getPunchCorrection();
                    return ApprovalContentPunchCorrection.builder().state(punchCorrection.getState()).time(new Date(punchCorrection.getTime() * 1000)).build();
                })
                .orElse(null);
    }

}
