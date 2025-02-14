package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.oa.application.helper.ApprovalAssemblerHelper;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
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
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/11/17 13:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalCorrection {

    @NotNull
    private CpId cpId;
    @NotNull
    private SpNo spNo;
    @NotEmpty
    private String state;
    @NotNull
    private Date correctionTime;

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

    public static ApprovalCorrection from(CpId cpId, String userName, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        String reason = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.TEXTAREA, "补卡事由");
        ApprovalContentPunchCorrection punchCorrection = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.PUNCH_CORRECTION, "补卡");
        ApprovalContentFile file = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.FILE, "附件");
        List<ApprovalComment> comments = ApprovalAssemblerHelper.getComments(approvalDetail);
        return ApprovalCorrection.builder().cpId(cpId).spNo(new SpNo(approvalDetail.getSpNo()))
                .correctionTime(punchCorrection.getTime()).state(punchCorrection.getState()).reason(reason).status(ApprovalStatus.from(approvalDetail.getSpStatus()))
                .userId(new WxUserId(approvalDetail.getApplier().getUserId())).userName(userName)
                .applyTime(new Date(approvalDetail.getApplyTime() * 1000)).file(file).comments(comments).build();
    }

    public List<WxMediaId> allMedias() {
        List<WxMediaId> mediaIds = new ArrayList<>();
        if (Objects.nonNull(file)) {
            mediaIds.addAll(file.getFileIds());
        }
        for (ApprovalComment comment : comments) {
            mediaIds.addAll(comment.getMediaIds());
        }
        return mediaIds;
    }

}
