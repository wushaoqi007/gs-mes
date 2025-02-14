package com.greenstone.mes.form.domain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.base.api.RemoteSystemService;
import com.greenstone.mes.external.application.service.ProcessDefinitionService;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessRevokeCmd;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.application.assembler.FormDataAssembler;
import com.greenstone.mes.form.domain.entity.CustomFormDataEntity;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.repository.CustomFormDataRepository;
import com.greenstone.mes.form.domain.service.AbstractFormDataService;
import com.greenstone.mes.form.domain.service.CustomFormDataService;
import com.greenstone.mes.form.dto.cmd.FormDataRemoveCmd;
import com.greenstone.mes.form.dto.cmd.FormDataRevokeCmd;
import com.greenstone.mes.form.dto.cmd.FormDataSaveCmd;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.dto.result.FormCommitResult;
import com.greenstone.mes.form.dto.result.FormDraftResult;
import com.greenstone.mes.form.infrastructure.annotation.FormService;
import com.greenstone.mes.form.infrastructure.mapper.FormDataMapper;
import com.greenstone.mes.form.infrastructure.persistence.FormDataPo;
import com.greenstone.mes.system.api.RemoteMenuService;
import com.greenstone.mes.system.dto.cmd.SerialNoNextCmd;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import com.greenstone.mes.system.dto.result.SerialNoR;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@FormService("custom_service")
@Slf4j
@Service
public class CustomFormDataServiceImpl extends AbstractFormDataService<CustomFormDataEntity, FormDataPo, FormDataMapper> implements CustomFormDataService {

    private final CustomFormDataRepository customFormDataRepository;
    private final FormDataAssembler formDataAssembler;
    private final RemoteSystemService systemService;
    private final RemoteMenuService menuService;
    private final ProcessInstanceService processService;
    private final ProcessDefinitionService definitionService;

    public CustomFormDataServiceImpl(CustomFormDataRepository customFormDataRepository, FormDataAssembler formDataAssembler,
                                     RemoteSystemService systemService, RemoteMenuService menuService, ProcessInstanceService processService,
                                     ProcessDefinitionService definitionService, ProcessInstanceService processInstanceService, FormHelper formHelper) {
        super(customFormDataRepository, processInstanceService, formHelper);
        this.customFormDataRepository = customFormDataRepository;
        this.formDataAssembler = formDataAssembler;
        this.systemService = systemService;
        this.menuService = menuService;
        this.processService = processService;
        this.definitionService = definitionService;
    }

    @Override
    public List<CustomFormDataEntity> query(FormDataQuery query) {
        return customFormDataRepository.queryEntityList(query);
    }

    public FormDraftResult draft(FormDataSaveCmd saveCmd) {
        saveCmd.setStatus(ProcessStatus.DRAFT);
        CustomFormDataEntity saveFormDataEntity = formDataAssembler.saveCmd2Entity(saveCmd);
        if (StrUtil.isBlank(saveCmd.getId())) {
            SerialNoR nextSn = systemService.getNextSn(SerialNoNextCmd.builder().type(saveCmd.getFormId()).prefix("").build());
            saveFormDataEntity.setSerialNo(nextSn.getSerialNo());
        }
        CustomFormDataEntity formDataEntity = customFormDataRepository.saveFormData(saveFormDataEntity);
        return formDataAssembler.entity2DraftResult(formDataEntity);
    }

    public FormCommitResult commit(FormDataSaveCmd saveCmd) {
        CustomFormDataEntity commitFormDataEntity = formDataAssembler.saveCmd2Entity(saveCmd);
        // 设置 serialNo
        if (StrUtil.isBlank(saveCmd.getId())) {
            SerialNoR nextSn = systemService.getNextSn(SerialNoNextCmd.builder().type(saveCmd.getFormId()).prefix("").build());
            commitFormDataEntity.setSerialNo(nextSn.getSerialNo());
        } else {
            CustomFormDataEntity existFormDataEntity = customFormDataRepository.getById(saveCmd.getFormId(), saveCmd.getId());
            if (existFormDataEntity == null) {
                throw new RuntimeException("选择的记录不存在");
            }
            commitFormDataEntity.setSerialNo(existFormDataEntity.getSerialNo());
        }
        // 若是流程表单，且有流程定义，则新建流程；否则直接保存数据
        MenuBriefResult form = menuService.getBriefForm(saveCmd.getFormId());
        Boolean isDefinitionExist = false;
        if (form.isUsingProcess()) {
            isDefinitionExist = definitionService.getDefinitionId(saveCmd.getFormId());
        }
        if (form.isUsingProcess() && isDefinitionExist) {
            commitFormDataEntity.setStatus(ProcessStatus.APPROVING);
            ProcessStartCmd processStartCmd = ProcessStartCmd.builder().formId(saveCmd.getFormId())
                    .formName(form.getMenuName())
                    .serialNo(commitFormDataEntity.getSerialNo()).build();
            ProcessRunResult runResult = processService.createAndRun(processStartCmd);
            commitFormDataEntity.setProcessInstanceId(runResult.getProcessInstanceId());
            commitFormDataEntity.setProcessDefinitionId(runResult.getProcessDefinitionId());
        } else {
            commitFormDataEntity.setStatus(ProcessStatus.COMMITTED);
        }
        CustomFormDataEntity formDataEntity = customFormDataRepository.saveFormData(commitFormDataEntity);
        return formDataAssembler.entity2CommitResult(formDataEntity);
    }

    @Override
    public void delete(FormDataRemoveCmd deleteCmd) {
        List<FormDataPo> dataDos = customFormDataRepository.getByIds(deleteCmd.getFormId(), deleteCmd.getIds());
        if (CollUtil.isNotEmpty(dataDos)) {
            boolean notDraftExist = dataDos.stream().anyMatch(fd -> fd.getStatus() != ProcessStatus.DRAFT);
            if (notDraftExist) {
                throw new RuntimeException("只能删除草稿状态的记录");
            }
            customFormDataRepository.deleteByIds(deleteCmd.getFormId(), deleteCmd.getIds());
        } else {
            throw new RuntimeException("要删除的记录不存在");
        }
    }

    @GlobalTransactional
    @Override
    public void revoke(FormDataRevokeCmd revokeCmd) {
        List<FormDataPo> dataDos = customFormDataRepository.getByIds(revokeCmd.getFormId(), revokeCmd.getIds());
        for (FormDataPo dataDo : dataDos) {
            processService.revokeProcess(ProcessRevokeCmd.builder().processInstanceId(dataDo.getProcessInstanceId()).build());
        }
        if (CollUtil.isNotEmpty(dataDos)) {
            boolean notDraftExist = dataDos.stream().anyMatch(fd -> fd.getStatus() != ProcessStatus.APPROVING);
            if (notDraftExist) {
                throw new RuntimeException("只能撤销审批中的记录");
            }
            customFormDataRepository.revokeByIds(revokeCmd.getFormId(), revokeCmd.getIds());
        } else {
            throw new RuntimeException("要撤销的记录不存在");
        }
    }

}
