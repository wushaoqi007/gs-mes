package com.greenstone.mes.machine.interfaces.rest;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.core.web.controller.BaseController;
import com.greenstone.mes.common.core.web.domain.AjaxResult;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceSendCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceSendVO;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceResult;
import com.greenstone.mes.machine.application.service.MachineInquiryPriceService;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tables/100000017")
public class MachineInquiryPriceApi extends BaseController {

    private final MachineInquiryPriceService inquiryPriceService;

    @GetMapping("/search")
    public AjaxResult search(@Validated MachinePartScanQuery query) {
        List<MachineInquiryPriceResult> result = inquiryPriceService.searchByScan(query);
        return AjaxResult.success(result);
    }

    @GetMapping("/part/scan")
    public AjaxResult partScan(@Validated MachinePartScanQuery query) {
        MachineRequirementDetail part = inquiryPriceService.scan(query);
        return AjaxResult.success(part);
    }

    @PostMapping("/send")
    public AjaxResult sendInquiryPrice(@RequestParam("xlsxFile") MultipartFile xlsxFile,
                                       @RequestParam("attachments") List<MultipartFile> attachments,
                                       @RequestParam("providerList") List<String> providerList,
                                       @RequestParam("deadline") String deadline) {
        log.info("Receive machine inquiry price request");
        try {
            LocalDate localDate = LocalDateTimeUtil.parseDate(deadline, "yyyy-MM-dd");
            deadline = LocalDateTimeUtil.format(localDate, "MM月dd日");
        } catch (DateTimeParseException e) {
            throw new ServiceException("请使用yyyy-MM-dd格式的截至日期");
        }
        log.info("组装服务数据对象");
        MachineInquiryPriceSendCmd sendCmd = MachineInquiryPriceSendCmd.builder().providerList(providerList)
                .attachments(attachments).xlsxFile(xlsxFile)
                .deadline(deadline).build();
        String filename = xlsxFile.getOriginalFilename();
        if (filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            // 将表格转为VO
            List<MachineInquiryPriceSendVO> importVOs;
            try {
                importVOs = new ExcelUtil<>(MachineInquiryPriceSendVO.class).toList(xlsxFile);
            } catch (Exception e) {
                throw new ServiceException("询价表格内容有误：请以yyyy/MM/dd日期格式填写加工纳期");
            }
            // 校验表格数据
            String validateResult = ValidationUtils.validate(importVOs);
            if (Objects.nonNull(validateResult)) {
                log.error(validateResult);
                throw new ServiceException("询价表格内容有误：" + validateResult);
            }
            sendCmd.setInquiryPriceParts(importVOs);
        } else {
            throw new ServiceException("请上传需要询价的表格，格式为xlsx或xls表格");
        }
        log.info("发送询价邮件");
        inquiryPriceService.sendInquiryPrice(sendCmd);
        log.info("询价邮件发送完成");
        return AjaxResult.success();
    }
}
