package com.greenstone.mes.material.interfaces.rest;

import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.material.application.dto.result.IssueScanResult;
import com.greenstone.mes.material.domain.service.MaterialIssueService;
import com.greenstone.mes.material.infrastructure.persistence.IssueInvPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 项目控制类
 *
 * @author wushaoqi
 * @date 2023-01-09-10:12
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/issue")
public class MaterialIssueApi extends BaseController {

    private final MaterialIssueService materialIssueService;

    @GetMapping("/scan")
    public AjaxResult listScanData(IssueInvPo issueInv) {
        List<IssueScanResult> issueScanResults = materialIssueService.listByMaterialCode(issueInv.getMaterialCode());
        return AjaxResult.success(issueScanResults);
    }

    @PostMapping("/import")
    public AjaxResult importIssue(MultipartFile file) {
        log.info("Receive importIssue request");
        try {
            Workbook wb = WorkbookFactory.create(file.getInputStream());
            materialIssueService.importMaterialIssue(wb, file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return AjaxResult.success("导入成功");
    }

}
