package com.greenstone.mes.machine.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.ValidationUtils;
import com.greenstone.mes.common.core.utils.poi.ExcelUtil;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.external.dto.result.MailSendResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.machine.application.assemble.MachineRequirementAssemble;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRequirementImportVO;
import com.greenstone.mes.machine.application.dto.cqe.cmd.MachineRequirementQrCodeSaveVO;
import com.greenstone.mes.machine.application.dto.result.MachineRequirementExportR;
import com.greenstone.mes.machine.application.helper.MachineHelper;
import com.greenstone.mes.machine.application.service.MachineRequirementService;
import com.greenstone.mes.machine.domain.entity.MachineRequirement;
import com.greenstone.mes.machine.domain.entity.MachineRequirementChangeReason;
import com.greenstone.mes.machine.domain.entity.MachineRequirementDetail;
import com.greenstone.mes.machine.domain.repository.MachineRequirementRepository;
import com.greenstone.mes.machine.infrastructure.constant.MachineParam;
import com.greenstone.mes.machine.infrastructure.mapper.MachineRequirementMapper;
import com.greenstone.mes.machine.infrastructure.persistence.MachineRequirementDO;
import com.greenstone.mes.mail.api.RemoteMailService;
import com.greenstone.mes.mail.cmd.MailAddress;
import com.greenstone.mes.mail.cmd.MailAttachment;
import com.greenstone.mes.mail.cmd.MailSendCmd;
import com.greenstone.mes.office.ces.constant.UserParamKey;
import com.greenstone.mes.system.api.RemoteParamService;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.system.consts.BusinessKey;
import com.greenstone.mes.system.domain.User;
import com.greenstone.mes.system.dto.cmd.UserParamSaveCmd;
import com.greenstone.mes.table.adapter.UserServiceAdapter;
import com.greenstone.mes.table.core.AbstractTableService;
import com.greenstone.mes.table.core.TableRepository;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wushaoqi
 * @date 2024-11-08-10:33
 */
@Slf4j
@TableFunction(id = "100000014", entityClass = MachineRequirement.class, poClass = MachineRequirementDO.class, updateReason = UpdateReason.NECESSARY, reasonClass = MachineRequirementChangeReason.class)
@Service
public class MachineRequirementServiceImpl extends AbstractTableService<MachineRequirement, MachineRequirementDO, MachineRequirementMapper> implements MachineRequirementService {

    private final MachineRequirementRepository requirementRepository;
    private final RemoteMailService mailService;
    private final RemoteParamService paramService;
    private final MachineRequirementAssemble requirementAssemble;
    private final RemoteFileService fileService;
    private final UserServiceAdapter userServiceAdapter;
    private final MachineHelper machineHelper;

    public MachineRequirementServiceImpl(TableRepository<MachineRequirement, MachineRequirementDO, MachineRequirementMapper> tableRepository,
                                         ApplicationEventPublisher eventPublisher, MachineRequirementRepository requirementRepository,
                                         RemoteMailService mailService, RemoteParamService paramService,
                                         MachineRequirementAssemble requirementAssemble,
                                         RemoteFileService fileService, UserServiceAdapter userServiceAdapter,
                                         MachineHelper machineHelper) {
        super(tableRepository, eventPublisher);
        this.requirementRepository = requirementRepository;
        this.mailService = mailService;
        this.paramService = paramService;
        this.requirementAssemble = requirementAssemble;
        this.fileService = fileService;
        this.userServiceAdapter = userServiceAdapter;
        this.machineHelper = machineHelper;
    }

    @Override
    public void importImpl(MultipartFile file, Map<String, Object> params) {
        log.info("Receive machine requirement import request");
        // 将表格转为VO
        List<MachineRequirementImportVO> importVOs = new ExcelUtil<>(MachineRequirementImportVO.class).toList(file);
        log.info("Import content size: {}", importVOs.size());
        // 将序号为空的数据排除(可能有空格等不可见的字符，导致存在数据但是并不需要导入的行)
        importVOs = importVOs.stream().filter(d -> StrUtil.isNotBlank(d.getSeqNum())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(importVOs)) {
            throw new ServiceException("数据序号不能为空。");
        }
        // 表格校验
        String validateResult = ValidationUtils.validate(importVOs);
        if (Objects.nonNull(validateResult)) {
            log.error(validateResult);
            throw new ServiceException(validateResult);
        }
        List<MachineRequirementDetail> parts = new ArrayList<>();
        for (MachineRequirementImportVO importVO : importVOs) {
            MachineRequirementDetail part = MachineRequirementDetail.builder().serialNo(importVO.getProcessOrderCode())
                    .projectCode(importVO.getProjectCode()).partCode(importVO.validAndGetPartCodeNameVersion().getCode())
                    .partName(importVO.validAndGetPartCodeNameVersion().getName()).partVersion(importVO.validAndGetPartCodeNameVersion().getVersion())
                    .perSet(importVO.getPartNumber().longValue()).setsNumber(importVO.getSetsNumber())
                    .processNumber(importVO.calProcessNumber()).paperNumber(importVO.getPaperNumber())
                    .surfaceTreatment(importVO.getSurfaceTreatment()).rawMaterial(importVO.getRawMaterial())
                    .weight(importVO.getWeight()).printDate(importVO.getPrintDate() == null ? LocalDateTime.now() : LocalDateTimeUtil.of(importVO.getPrintDate()))
                    .designer(importVO.getDesigner()).remark(importVO.getRemark())
                    .hierarchy(importVO.getHierarchy())
                    .build();
            parts.add(part);
        }
        MachineRequirement requirement = MachineRequirement.builder().serialNo(importVOs.get(0).getProcessOrderCode())
                .projectCode(importVOs.get(0).getProjectCode()).parts(parts).build();
        // 通用校验
        machineHelper.requirementGeneralValidate(requirement);
        insertOrUpdate(requirement);
    }

    @Override
    public MultipartFile exportImpl(MachineRequirement requirement) {
        List<MachineRequirement> entities = getEntities(requirement);
        List<MachineRequirementDetail> exportParts = new ArrayList<>();
        for (MachineRequirement entity : entities) {
            exportParts.addAll(entity.getParts());
        }
        List<MachineRequirementExportR> results = requirementAssemble.toExportDataList(exportParts);
        return uploadFile(results);
    }

    @Override
    public String generateSerialNo(MachineRequirement requirement) {
        if (StrUtil.isNotBlank(requirement.getSerialNo())) {
            if (requirementRepository.isExist(requirement.getSerialNo())) {
                throw new ServiceException("申请单号已存在，不能使用此申请单号新建");
            }
            return requirement.getSerialNo();
        } else {
            throw new ServiceException("申请单号不能为空");
        }
    }

    @Override
    public void beforeCreate(MachineRequirement requirement) {
        if (CollUtil.isNotEmpty(requirement.getParts())) {
            requirement.getParts().forEach(d -> {
                d.setSerialNo(requirement.getSerialNo());
                d.setProjectCode(requirement.getProjectCode());
                d.setSetsNumber(d.getSetsNumber() == null ? 1 : d.getSetsNumber());
                d.setPerSet(d.getPerSet() == null ? d.getProcessNumber() : d.getPerSet());
                d.setCreateBy(SecurityUtils.getUserId());
                d.setCreateTime(LocalDateTime.now());
            });
        }
        machineHelper.requirementPageValidate(requirement);
        machineHelper.requirementGeneralValidate(requirement);
        validateByQuery(requirement);
    }

    @Override
    public void beforeUpdate(MachineRequirement requirement) {
        if (CollUtil.isNotEmpty(requirement.getParts())) {
            requirement.getParts().forEach(d -> {
                d.setSerialNo(requirement.getSerialNo());
                d.setProjectCode(requirement.getProjectCode());
                d.setSetsNumber(d.getSetsNumber() == null ? 1 : d.getSetsNumber());
                d.setPerSet(d.getPerSet() == null ? d.getProcessNumber() : d.getPerSet());
            });
        }
        machineHelper.requirementPageValidate(requirement);
        machineHelper.requirementGeneralValidate(requirement);
        validateByQuery(requirement);
    }


    @Override
    public void afterCreate(MachineRequirement requirement) {
        // 保存审批人和抄送人数据
        saveUserParam(requirement);
    }

    @Override
    public void afterUpdate(MachineRequirement requirement) {
        // 保存审批人和抄送人数据
        saveUserParam(requirement);
    }

    @Override
    public void beforeStartProcess(MachineRequirement requirement) {
//        requirement.setDetailLink(MachineParam.MACHINE_REQUIREMENT_LINK + requirement.getSerialNo());
    }

    @Override
    public void setEntityUser(MachineRequirement requirement) {
        super.setEntityUser(requirement);
        if (requirement.getConfirmBy() != null) {
            requirement.setConfirmByUser(userServiceAdapter.getUserById(requirement.getConfirmBy()));
        }
    }

    @Override
    public void updateApprovalChange(ApprovalChangeMsg msg) {
        super.updateApprovalChange(msg);
        // 审批完成发生通知邮件
        if (BusinessKey.MACHINING_APPLY.equals(msg.getBusinessKey())) {
            approval(msg);
        }

    }


    @Override
    public void approval(ApprovalChangeMsg approvalChangeMsg) {
        if (approvalChangeMsg.getStatus() == ProcessStatus.FINISH) {
            MachineRequirement requirement = requirementRepository.getEntity(approvalChangeMsg.getItemId());
            if (requirement.getProcessStartBy() != null) {
                requirement.setProcessStartUser(userServiceAdapter.getUserById(requirement.getProcessStartBy()));
            }
            // 审批完成发送邮件
            sendApprovalMail(requirement, approvalChangeMsg);
        }
    }

    @Override
    public void mailResult(MailSendResult mailSendResult) {
        if (StrUtil.isNotEmpty(mailSendResult.getErrorMsg()) && mailSendResult.getErrorMsg().length() > 255) {
            // 错误信息太长，截取中文部分
            mailSendResult.setErrorMsg(mailSendResult.getErrorMsg().substring(0, mailSendResult.getErrorMsg().indexOf(":")));
        }
        requirementRepository.saveMailResult(mailSendResult);
    }

    public MockMultipartFile uploadFile(List<MachineRequirementExportR> results) {
        String fileName = "机加工申请" + System.currentTimeMillis();
        MockMultipartFile multipartFile;
        try {
            // 使用EasyExcel将文件写到流
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            EasyExcel.write(outStream, MachineRequirementExportR.class).sheet(fileName).doWrite(results);
            // 将输出流转为 multipartFile 并上传
            multipartFile = new MockMultipartFile("file", fileName + ".xlsx", null, outStream.toByteArray());
            outStream.close();
        } catch (IOException e) {
            log.error(fileName + "导出错误:" + e.getMessage());
            throw new RuntimeException(fileName + "导出错误");
        }
        return multipartFile;
    }

    public void insertOrUpdate(MachineRequirement requirement) {
        requirement.setLocked(false);
        requirement.setDataStatus(TableConst.DataStatus.DRAFT);
        requirement.getParts().forEach((p -> {
            p.setCreateBy(SecurityUtils.getUserId());
            p.setCreateTime(LocalDateTime.now());
        }));
        MachineRequirement findEntity = requirementRepository.getBySerialNo(requirement.getSerialNo());
        if (findEntity != null) {
            if (findEntity.getDataStatus() != TableConst.DataStatus.DRAFT) {
                throw new ServiceException("申请单号已存在");
            }
            requirement.setId(findEntity.getId());
            if (CollUtil.isNotEmpty(findEntity.getParts())) {
                for (MachineRequirementDetail part : requirement.getParts()) {
                    // 同一个申请单中，一个零件号版本只能出现一次。
                    Optional<MachineRequirementDetail> findPart = findEntity.getParts().stream()
                            .filter(p -> p.getPartCode().equals(part.getPartCode()) && p.getPartVersion().equals(part.getPartVersion())).findFirst();
                    if (findPart.isPresent()) {
                        throw new ServiceException(StrUtil.format("此零件已存在，零件号/版本：{}/{}", part.getPartCode(), part.getPartVersion()));
                    }
                }
                // 导入的零件，合并到原单据中
                requirement.getParts().addAll(findEntity.getParts());
            }
            // 这里不使用通用updated原因：手机扫码不是通用api进来的，会报错.TableThreadLocal.getTableMeta()" is null
            // 如果需要修改为通用的，则需要
            // 1、把手机扫码的接口改成/tables/{tableId}/items的前缀格式，按照通用接口的格式写
            // 2、上面的校验可去除，requirementPageValidate和validateByQuery的校验需要按状态更改
            // 3、其他方面：如审批人和抄送人为空保存数据会报错？
            requirementRepository.update(requirement);
        } else {
            requirementRepository.insert(requirement);
        }
    }


    @Override
    public void saveRequirementFromMobile(MachineRequirementQrCodeSaveVO qrCodeSaveVO) {
        MachineRequirementDetail part = MachineRequirementDetail.builder().serialNo(qrCodeSaveVO.getSerialNo())
                .projectCode(qrCodeSaveVO.getProjectCode()).partCode(qrCodeSaveVO.getMaterialCode())
                .partName(qrCodeSaveVO.getMaterialName()).partVersion(qrCodeSaveVO.getVersion())
                .perSet(qrCodeSaveVO.getPerSet()).setsNumber(qrCodeSaveVO.getSetsNumber())
                .processNumber(qrCodeSaveVO.calProcessNumber()).paperNumber(qrCodeSaveVO.getPaperNumber())
                .surfaceTreatment(qrCodeSaveVO.getSurfaceTreatment()).rawMaterial(qrCodeSaveVO.getRawMaterial())
                .weight(qrCodeSaveVO.getWeight()).printDate(qrCodeSaveVO.getPrintDate() == null ? LocalDateTime.now() : LocalDateTimeUtil.of(qrCodeSaveVO.getPrintDate()))
                .designer(qrCodeSaveVO.getDesigner()).remark(qrCodeSaveVO.getRemark())
                .hierarchy(qrCodeSaveVO.getHierarchy())
                .build();
        // 版本必须是大写的V加一个数字
        if (part.getPartVersion().charAt(0) != 'V') {
            throw new ServiceException("零件版本必须是大写的V");
        }
        if (part.getPartVersion().length() != 2 || !CharUtil.isNumber(part.getPartVersion().charAt(1))) {
            throw new ServiceException("版本必须是大写的V加一个数字");
        }
        List<MachineRequirementDetail> parts = new ArrayList<>();
        parts.add(part);
        MachineRequirement requirement = MachineRequirement.builder().serialNo(qrCodeSaveVO.getSerialNo())
                .projectCode(qrCodeSaveVO.getProjectCode()).parts(parts).build();
        insertOrUpdate(requirement);
    }


    private void sendApprovalMail(MachineRequirement requirement, ApprovalChangeMsg approvalChangeMsg) {
        log.info("开始发送机加工申请审批邮件通知");
        // 抄送人和配置的默认抄送人
        List<MailAddress> cc = machineHelper.defaultEmailCCOfRequirementApproval();
        List<Long> copyTo = requirement.getCopyTo();
        for (Long userId : copyTo) {
            User user = userServiceAdapter.getUserById(userId);
            cc.add(new MailAddress(user.getEmail(), user.getNickName()));
        }
        User approver = userServiceAdapter.getUserById(requirement.getApprovers().get(0));
        // 申请人为流程开始人
        User applier = userServiceAdapter.getUserById(requirement.getProcessStartBy());
        // 默认抄送自己和审批人
        cc.add(new MailAddress(approver.getEmail(), approver.getNickName()));
        cc.add(new MailAddress(applier.getEmail(), applier.getNickName()));
        cc = cc.stream().filter(address -> StrUtil.isNotBlank(address.getAddress())).distinct().toList();

        // 接收人:未配置默认收件人则发送给审批人
        List<MailAddress> recipients = machineHelper.defaultEmailRecipientOfRequirementApproval();
        if (CollUtil.isEmpty(recipients)) {
            recipients.add(new MailAddress(approver.getEmail(), approver.getNickName()));
        }
        log.info(StrUtil.format("邮件接收人：{}", recipients));
        log.info(StrUtil.format("需要抄送的人：{}", cc));

        String title = StrUtil.format("【{} {}】{}", requirement.getProcessStartUser().getNickName(), requirement.getSerialNo(), requirement.getTitle());
        // 添加审批时的备注
        String remark = StrFormatter.format("审批人：{} <br> 审批内容：{} <br><br>——————————————————————————————————<br><br>", approver.getNickName(), approvalChangeMsg.getRemark() == null ? "无" : approvalChangeMsg.getRemark());
        String content = remark + requirement.getContent();
        List<MachineRequirementExportR> results = requirementAssemble.toExportDataList(requirement.getParts());
        MockMultipartFile mockMultipartFile = uploadFile(results);
        SysFile sysFile = fileService.upload(mockMultipartFile, 15).getData();
        List<MailAttachment> mailAttachments = List.of(new MailAttachment(sysFile.getName(), sysFile.getPath()));
        log.info(StrUtil.format("邮件标题：{}", title));
        log.info(StrUtil.format("邮件内容：{}", content));
        log.info(StrUtil.format("邮件附件个数：{}", mailAttachments.size()));
        MailSendCmd mailSendCmd = MailSendCmd.builder().businessKey(BusinessKey.MACHINING_APPLY).serialNo(requirement.getSerialNo()).subject(title).content(content).attachments(mailAttachments).to(recipients).cc(cc).html(true).build();
        if (StrUtil.isNotEmpty(MachineParam.MACHINE_REQUIREMENT_EMAIL_SENDER)) {
            mailSendCmd.setSender(MachineParam.MACHINE_REQUIREMENT_EMAIL_SENDER);
        }
        mailService.sendAsync(mailSendCmd);
        log.info("完成机加工申请审批邮件通知发送");
    }

    private void saveUserParam(MachineRequirement requirement) {
        UserParamSaveCmd approverParam = UserParamSaveCmd.builder().billType("machining_apply").paramKey(UserParamKey.approvers).paramValue(StrUtil.join(",", requirement.getApprovers())).build();
        UserParamSaveCmd copyParam = UserParamSaveCmd.builder().billType("machining_apply").paramKey(UserParamKey.copyTo).paramValue(StrUtil.join(",", requirement.getCopyTo())).build();
        List<UserParamSaveCmd> saveCmds = List.of(approverParam, copyParam);
        paramService.saveUserParam(saveCmds);
    }


    public void validateByQuery(MachineRequirement requirement) {
        for (Long userId : requirement.getApprovers()) {
            User userinfo = userServiceAdapter.getUserById(userId);
            if (userinfo == null) {
                throw new ServiceException(StrUtil.format("审批人未找到，userId:{}", userId));
            }
        }
        for (Long userId : requirement.getCopyTo()) {
            User userinfo = userServiceAdapter.getUserById(userId);
            if (userinfo == null) {
                throw new ServiceException(StrUtil.format("抄送人未找到，userId:{}", userId));
            }
        }
    }
}
