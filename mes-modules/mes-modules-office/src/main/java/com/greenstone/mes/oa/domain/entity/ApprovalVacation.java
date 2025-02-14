package com.greenstone.mes.oa.domain.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.greenstone.mes.oa.application.helper.ApprovalAssemblerHelper;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.enums.ContentType;
import com.greenstone.mes.oa.infrastructure.enums.VacationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/17 13:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalVacation {

    @NotNull
    private CpId cpId;
    @NotNull
    private SpNo spNo;
    @NotNull
    private VacationType type;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;

    private String reason;
    @NotNull
    private ApprovalStatus status;
    @NotNull
    private WxUserId userId;
    @NotEmpty
    private String userName;
    @NotNull
    private Date applyTime;

    private ApprovalContentFile file;

    private List<ApprovalComment> comments;

    public static ApprovalVacation from(CpId cpId, String userName, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        String reason = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.TEXTAREA, "请假事由");
        ApprovalContentVacation vacation = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.VACATION, "请假类型");
        ApprovalContentFile file = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.FILE, "说明附件");
        List<ApprovalComment> comments = ApprovalAssemblerHelper.getComments(approvalDetail);
        VacationType type = VacationType.getByName(vacation.getType().getName());
        Date endTime = vacation.getDateRange().getEnd();
        // 按天计算的请假，结束时间+12小时；因为企业微信的上午为0点，下午为12点
        if ("按天".equals(type.getUnit())) {
            endTime = new DateTime(endTime).offset(DateField.HOUR_OF_DAY, +12);
        }
        return ApprovalVacation.builder().cpId(cpId).spNo(new SpNo(approvalDetail.getSpNo())).type(type)
                .startTime(vacation.getDateRange().getBegin()).endTime(endTime)
                .reason(reason).status(ApprovalStatus.from(approvalDetail.getSpStatus()))
                .userId(new WxUserId(approvalDetail.getApplier().getUserId())).userName(userName)
                .applyTime(new Date(approvalDetail.getApplyTime() * 1000)).file(file).comments(comments).build();
    }

    public List<WxMediaId> allMedias() {
        List<WxMediaId> mediaIds = new ArrayList<>();
        if (Objects.nonNull(file)) {
            mediaIds.addAll(file.getFileIds());
        }
        if (CollUtil.isNotEmpty(comments)) {
            for (ApprovalComment comment : comments) {
                mediaIds.addAll(comment.getMediaIds());
            }
        }
        return mediaIds;
    }

}
