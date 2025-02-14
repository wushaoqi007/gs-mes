package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.application.helper.ApprovalAssemblerHelper;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.enums.ContentType;
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

/**
 * @author gu_renkai
 * @date 2022/11/17 13:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalNight {

    @NotNull
    private CpId cpId;
    @NotNull
    private SpNo spNo;
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

    private List<ApprovalComment> comments;

    public static ApprovalNight from(CpId cpId, String userName, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        String reason = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.TEXTAREA, "夜班事由");
        Date startTime = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.DATE, "开始时间");
        Date endTime = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.DATE, "结束时间");
        List<ApprovalComment> comments = ApprovalAssemblerHelper.getComments(approvalDetail);
        return ApprovalNight.builder().cpId(cpId).spNo(new SpNo(approvalDetail.getSpNo()))
                .startTime(startTime).endTime(endTime).reason(reason).status(ApprovalStatus.from(approvalDetail.getSpStatus()))
                .userId(new WxUserId(approvalDetail.getApplier().getUserId())).userName(userName)
                .applyTime(new Date(approvalDetail.getApplyTime() * 1000)).comments(comments).build();
    }

    public List<WxMediaId> allMedias() {
        List<WxMediaId> mediaIds = new ArrayList<>();
        for (ApprovalComment comment : comments) {
            mediaIds.addAll(comment.getMediaIds());
        }
        return mediaIds;
    }

}
