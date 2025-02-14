package com.greenstone.mes.oa.interfaces.rest;

import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalCheckedTakeCommitCmd;
import com.greenstone.mes.external.workwx.dto.cmd.WxApprovalFinishedCommitCmd;
import com.greenstone.mes.oa.domain.external.ExternalWxApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wushaoqi
 * @date 2024-07-09-14:21
 */
@Slf4j
@RestController
@RequestMapping("/wx/approval")
public class WxApprovalController {
    @Autowired
    private ExternalWxApprovalService approvalService;


    @PostMapping("/commit/check/take")
    public AjaxResult commitCheckTakeApproval(@RequestBody WxApprovalCheckTakeCommitCmd command) {
        log.info("发送质检取件的企业微信审批：{}", command);
        String spNo = approvalService.commitCheckTakeApproval(command);
        return AjaxResult.success(spNo);
    }

    @PostMapping("/commit/checked/take")
    public AjaxResult commitCheckedTakeApproval(@RequestBody WxApprovalCheckedTakeCommitCmd command) {
        log.info("发送合格品取件的企业微信审批：{}", command);
        String spNo = approvalService.commitCheckedTakeApproval(command);
        return AjaxResult.success(spNo);
    }

    @PostMapping("/commit/finished")
    public AjaxResult commitFinishedApproval(@RequestBody WxApprovalFinishedCommitCmd command) {
        log.info("发送出库的企业微信审批：{}", command);
        String spNo = approvalService.commitFinishedApproval(command);
        return AjaxResult.success(spNo);
    }
}
