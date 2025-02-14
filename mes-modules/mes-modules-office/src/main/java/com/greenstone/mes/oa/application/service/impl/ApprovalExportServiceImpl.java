package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.application.dto.attendance.ApprovalLeave;
import com.greenstone.mes.oa.application.dto.attendance.result.LeaveApprovalExport;
import com.greenstone.mes.oa.application.service.ApprovalExportService;
import com.greenstone.mes.oa.application.wrapper.ApprovalWrapper;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxOaService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.oa.WxCpApprovalDetailResult;
import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentValue;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class ApprovalExportServiceImpl implements ApprovalExportService {

    private final ApprovalWrapper approvalWrapper;

    private final WxOaService externalWxOaService;

    private final WxDeptService externalWxDeptService;

    private final WxUserService externalWxUserService;

    @Override
    public List<LeaveApprovalExport> getLeaveApprovalExportData(Date start, Date end, String cpId) {
        List<ApprovalLeave> approvalLeaveList = approvalWrapper.getApprovalLeaveList(start.getTime() / 1000, end.getTime() / 1000, null, cpId);
        log.info("find {} leave approval", approvalLeaveList.size());

        List<LeaveApprovalExport> exportList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (ApprovalLeave approvalLeave : approvalLeaveList) {

            WxCpApprovalDetailResult approvalDetail = externalWxOaService.getApprovalDetail(new CpId(cpId), new SpNo(approvalLeave.getSpNo()));
            LeaveApprovalExport export = new LeaveApprovalExport();
            exportList.add(export);

            WxCpUser wxCpUser = externalWxUserService.getUser(new CpId(cpId), new WxUserId(approvalLeave.getUserId()));
            externalWxDeptService.getDept(new CpId(cpId), new WxDeptId(wxCpUser.getDepartIds()[0]));

            export.setName(wxCpUser.getName());
            export.setType(approvalLeave.getType());
            export.setStartTime(sdf.format(new Date(approvalLeave.getStart() * 1000)));
            export.setEndTime(sdf.format(new Date(approvalLeave.getEnd() * 1000)));
            export.setReason(approvalLeave.getReason());
            export.setApprover(wxCpUser.getName());

            List<String> mediaIds = new ArrayList<>();
            List<ApplyDataContent> contents = approvalDetail.getInfo().getApplyData().getContents();
            for (ApplyDataContent content : contents) {
                if ("File".equals(content.getControl())) {
                    List<ContentValue.File> files = content.getValue().getFiles();
                    if (CollUtil.isNotEmpty(files)) {
                        for (ContentValue.File file : files) {
                            mediaIds.add(file.getFileId());
                        }
                    }
                }
            }
            export.setFileIds(mediaIds);
        }
        return exportList;
    }

}
