package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.enums.MachineError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.common.utils.DateUtil;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.machine.application.assemble.MachineInquiryPriceAssemble;
import com.greenstone.mes.machine.application.assemble.MachineRequirementAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceSendCmd;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineInquiryPriceSendVO;
import com.greenstone.mes.machine.application.dto.cqe.query.MachinePartScanQuery;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceExportR;
import com.greenstone.mes.machine.application.dto.result.MachineInquiryPriceResult;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineInquiryPriceService;
import com.greenstone.mes.machine.domain.entity.*;
import com.greenstone.mes.machine.domain.repository.MachineInquiryPriceRepository;
import com.greenstone.mes.machine.domain.repository.MachineProviderRepository;
import com.greenstone.mes.machine.domain.repository.MachineRequirementOldRepository;
import com.greenstone.mes.machine.infrastructure.enums.InquiryPriceStatus;
import com.greenstone.mes.machine.infrastructure.mapper.MachineInquiryPriceMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineInquiryPriceDO;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailAttachment;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.market.infrastructure.config.BuyerConfig;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.consts.BusinessKey;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.SerialNoR;
import com.greenstone.mes.table.core.AbstractTableService;
import com.greenstone.mes.table.core.TableRepository;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@TableFunction(id = "100000017", entityClass = MachineInquiryPrice.class, poClass = MachineInquiryPriceDO.class, updateReason = UpdateReason.NEVER)
@Service
public class MachineInquiryPriceServiceImpl extends AbstractTableService<MachineInquiryPrice, MachineInquiryPriceDO, MachineInquiryPriceMapper> implements MachineInquiryPriceService {

    private final MachineRequirementOldRepository requirementRepository;
    private final MachineInquiryPriceRepository inquiryPriceRepository;
    private final MachineInquiryPriceAssemble inquiryPriceAssemble;
    private final MachineRequirementAssemble requirementAssemble;
    private final RemoteSystemService systemService;
    private final MachineHelper machineHelper;
    private final RemoteMailService mailService;
    private final MachineProviderRepository providerRepository;
    private final TemplateEngine templateEngine;
    private final RemoteFileService fileService;
    private final BuyerConfig buyerConfig;

    public MachineInquiryPriceServiceImpl(TableRepository<MachineInquiryPrice, MachineInquiryPriceDO, MachineInquiryPriceMapper> tableRepository,
                                          ApplicationEventPublisher eventPublisher, MachineRequirementOldRepository requirementRepository,
                                          MachineInquiryPriceRepository inquiryPriceRepository, MachineInquiryPriceAssemble inquiryPriceAssemble,
                                          MachineRequirementAssemble requirementAssemble, RemoteSystemService systemService,
                                          MachineHelper machineHelper, RemoteMailService mailService, MachineProviderRepository providerRepository,
                                          TemplateEngine templateEngine, RemoteFileService fileService, BuyerConfig buyerConfig) {
        super(tableRepository, eventPublisher);
        this.requirementRepository = requirementRepository;
        this.inquiryPriceRepository = inquiryPriceRepository;
        this.inquiryPriceAssemble = inquiryPriceAssemble;
        this.requirementAssemble = requirementAssemble;
        this.systemService = systemService;
        this.machineHelper = machineHelper;
        this.mailService = mailService;
        this.providerRepository = providerRepository;
        this.templateEngine = templateEngine;
        this.fileService = fileService;
        this.buyerConfig = buyerConfig;
    }

    @Override
    public String generateSerialNo(MachineInquiryPrice inquiryPrice) {
        if (StrUtil.isNotBlank(inquiryPrice.getSerialNo())) {
            return inquiryPrice.getSerialNo();
        } else {
            // 设置单号
            SerialNoNextCmd nextCmd =
                    SerialNoNextCmd.builder().type("machine_inquiry_price").prefix("MIP" + DateUtil.dateSerialStrNow()).build();
            SerialNoR nextSn = systemService.getNextSn(nextCmd);
            return nextSn.getSerialNo();
        }
    }

    @Override
    public void beforeCreate(MachineInquiryPrice inquiryPrice) {
        validAndAssembleInquiryPrice(inquiryPrice);
        inquiryPrice.setHandleStatus(InquiryPriceStatus.UNQUOTED_PRICE);
        inquiryPrice.getParts().forEach(p -> {
            p.setCreateBy(SecurityUtils.getUserId());
            p.setCreateTime(LocalDateTime.now());
        });
    }

    @Override
    public void beforeUpdate(MachineInquiryPrice inquiryPrice) {
        validAndAssembleInquiryPrice(inquiryPrice);
    }

    @Override
    public void afterSubmit(MachineInquiryPrice inquiryPrice) {
        // 修改申请单已扫描图纸数
        requirementRepository.updateScannedPaperNumber(inquiryPrice.getParts());
    }

    @Override
    public MultipartFile exportImpl(MachineInquiryPrice inquiryPrice) {
        List<MachineInquiryPrice> entities = getEntities(inquiryPrice);
        List<MachineInquiryPriceExportR> results = new ArrayList<>();
        for (MachineInquiryPrice entity : entities) {
            List<MachineInquiryPriceExportR> machineInquiryPriceExportRS = inquiryPriceAssemble.toMachineInquiryPriceExportRS(entity.getParts());
            results.addAll(machineInquiryPriceExportRS);
        }
        return uploadFile(results);
    }

    @Override
    public List<MachineInquiryPriceResult> searchByScan(MachinePartScanQuery query) {
        List<MachineInquiryPrice> machineInquiryPrices = inquiryPriceRepository.selectListByScan(query);
        return inquiryPriceAssemble.toMachineInquiryPriceRs(machineInquiryPrices);
    }


    @Override
    public MachineRequirementDetail scan(MachinePartScanQuery query) {
        MachineRequirementDO requirementDO = requirementRepository.selectBySerialNo(query.getSerialNo());
        if (Objects.isNull(requirementDO)) {
            throw new ServiceException(MachineError.E200106, StrUtil.format("申请单号：{}", query.getSerialNo()));
        }
        if (requirementDO.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
            throw new ServiceException(StrUtil.format("申请单未生效，无法询价。申请单号：{}", query.getSerialNo()));
        }
        MachineRequirementDetail requirementDetail = requirementRepository.selectPart(query.getProjectCode(), query.getSerialNo(), query.getPartCode(), query.getPartVersion());
        if (Objects.isNull(requirementDetail)) {
            throw new ServiceException(MachineError.E200122, StrUtil.format("申请单号：{}，项目号：{}，零件号/版本：{}/{}", query.getSerialNo(), query.getProjectCode(),
                    query.getPartCode(), query.getPartVersion()));
        }
        return requirementDetail;
    }

    @Override
    public void sendInquiryPrice(MachineInquiryPriceSendCmd sendCmd) {
        // 设置Thymeleaf上下文
        Context context = new Context();
        context.setVariable("deadline", sendCmd.getDeadline());
        context.setVariable("inquiryPriceParts", sendCmd.getInquiryPriceParts());
        // 生成HTML内容
        String htmlContent = templateEngine.process("emailTemplate.html", context);
        // 附件
        List<MailAttachment> attachments = new ArrayList<>();
        try {
            for (MultipartFile attachment : sendCmd.getAttachments()) {
                R<SysFile> attachmentFile = fileService.upload(attachment, 1);
                attachments.add(MailAttachment.builder().name(attachmentFile.getData().getName()).path(attachmentFile.getData().getPath()).build());
            }
            R<SysFile> xlsFile = fileService.upload(sendCmd.getXlsxFile(), 1);
            attachments.add(MailAttachment.builder().name(xlsFile.getData().getName()).path(xlsFile.getData().getPath()).build());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("附件服务器错误，请联系管理员！");
        }

        // 发送询价单邮件
        for (String providerName : sendCmd.getProviderList()) {
            MachineProvider provider = providerRepository.findByFullName(providerName);
            String title = StrUtil.format("询价单_{}", provider.getName());
            List<String> cc = machineHelper.inquiryPriceEmailCC();
            List<MailAddress> ccMails = cc.stream().map(c -> new MailAddress(c, null)).collect(Collectors.toList());
            MailSendCmd mailSendCmd = MailSendCmd.builder().businessKey(BusinessKey.MACHINING_INQUIRY).serialNo(UUID.randomUUID().toString())
                    .sender(buyerConfig.getWxcg())
                    .subject(title).content(htmlContent)
                    .attachments(attachments)
                    .to(List.of(new MailAddress(provider.getEmail(), null)))
                    .cc(ccMails)
                    .html(true).build();
            mailService.sendAsync(mailSendCmd);
        }

        // 修改询价单状态
        List<String> serialNos = sendCmd.getInquiryPriceParts().stream().map(MachineInquiryPriceSendVO::getSerialNo).toList();
        inquiryPriceRepository.handleStatusChange(serialNos, InquiryPriceStatus.QUOTED_PRICE);

    }

    public void validAndAssembleInquiryPrice(MachineInquiryPrice inquiryPrice) {
        machineHelper.inquiryPriceGeneralValidate(inquiryPrice);
        int categoryTotal = inquiryPrice.getParts().size();
        AtomicLong partTotal = new AtomicLong();
        AtomicInteger paperTotal = new AtomicInteger();
        Map<String, List<MachineInquiryPriceDetail>> groupByRequirementSerialNo = inquiryPrice.getParts().stream().collect(Collectors.groupingBy(MachineInquiryPriceDetail::getRequirementSerialNo));
        groupByRequirementSerialNo.forEach((requirementSerialNo, partList) -> {
            MachineRequirement machineRequirement = requirementRepository.detail(requirementSerialNo);
            if (machineRequirement.getDataStatus() != TableConst.DataStatus.EFFECTIVE) {
                throw new ServiceException(StrUtil.format("申请单未生效，无法询价。申请单号：{}", requirementSerialNo));
            }
            for (MachineInquiryPriceDetail part : partList) {
                MachineRequirementDetail machineRequirementDetail = machineHelper.existInMachineRequirement(machineRequirement, part.getProjectCode(), part.getPartCode(), part.getPartVersion());
                part.setSerialNo(inquiryPrice.getSerialNo());
                part.setHierarchy(machineRequirementDetail.getHierarchy());
                part.setSurfaceTreatment(machineRequirementDetail.getSurfaceTreatment());
                part.setRawMaterial(machineRequirementDetail.getRawMaterial());
                part.setWeight(machineRequirementDetail.getWeight());
                part.setDesigner(machineRequirementDetail.getDesigner());
                part.setRemark(machineRequirementDetail.getRemark());
                part.setCreateBy(SecurityUtils.getUserId());
                part.setCreateTime(LocalDateTime.now());
                partTotal.addAndGet(part.getPartNumber());
                paperTotal.addAndGet(part.getPaperNumber());
            }
        });
        inquiryPrice.setCategoryTotal(categoryTotal);
        inquiryPrice.setPartTotal(partTotal.get());
        inquiryPrice.setPaperTotal(paperTotal.get());
    }

    public MockMultipartFile uploadFile(List<MachineInquiryPriceExportR> results) {
        String fileName = "询价单" + System.currentTimeMillis();
        MockMultipartFile multipartFile;
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineInquiryPriceExportR.class).sheet(fileName).doWrite(results);
            // 将输出流转为 multipartFile 并上传
            multipartFile = new MockMultipartFile("file", fileName + ".xlsx", null, outStream.toByteArray());
            outStream.close();
        } catch (IOException e) {
            log.error(fileName + "导出错误:" + e.getMessage());
            throw new RuntimeException(fileName + "导出错误");
        }
        return multipartFile;
    }
}
