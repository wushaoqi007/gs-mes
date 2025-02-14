package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import com.greenstone.mes.oa.application.helper.ApprovalAssemblerHelper;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.oa.infrastructure.enums.ApprovalStatus;
import com.greenstone.mes.oa.infrastructure.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalCheckTake {

    @NotNull
    private CpId cpId;
    @NotNull
    private SpNo spNo;
    @NotEmpty
    private String serialNo;
    @NotNull
    private ApprovalStatus status;
    @NotNull
    private Date applyTime;
    @NotEmpty
    private String applierName;
    @NotNull
    private Date takeTime;
    @NotEmpty
    private String takeBy;
    @NotEmpty
    private String sponsor;
    @NotNull
    private WxUserId takeById;
    @NotNull
    private WxUserId sponsorId;

    private List<ApprovalComment> comments;

    public static ApprovalCheckTake from(CpId cpId, String userName, WxCpApprovalDetailResult.WxCpApprovalDetail approvalDetail) {
        String serialNo = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.Text, "关联取件单");
        Date takeTime = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.DATE, "日期");
        ApprovalMembers takeBys = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.Contact, "取件人");
        ApprovalMembers sponsors = ApprovalAssemblerHelper.getContent(approvalDetail, ContentType.Contact, "经手人");
        String takeBy = takeBys.getMembers().get(0).getName();
        String takeById = takeBys.getMembers().get(0).getUserId();
        String sponsor = sponsors.getMembers().get(0).getName();
        String sponsorId = sponsors.getMembers().get(0).getUserId();
        List<ApprovalComment> comments = ApprovalAssemblerHelper.getComments(approvalDetail);
        return ApprovalCheckTake.builder().cpId(cpId).spNo(new SpNo(approvalDetail.getSpNo()))
                .status(ApprovalStatus.from(approvalDetail.getSpStatus())).serialNo(serialNo)
                .takeBy(takeBy).takeById(new WxUserId(takeById)).takeTime(takeTime)
                .sponsor(sponsor).sponsorId(new WxUserId(sponsorId))
                .applyTime(new Date(approvalDetail.getApplyTime() * 1000)).applierName(userName)
                .comments(comments).build();
    }


}
