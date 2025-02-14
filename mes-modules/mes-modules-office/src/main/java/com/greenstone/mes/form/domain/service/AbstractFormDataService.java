package com.greenstone.mes.form.domain.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.external.application.service.ProcessInstanceService;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.cmd.ProcessStartCmd;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.BaseFormDataEntity;
import com.greenstone.mes.form.domain.helper.FormHelper;
import com.greenstone.mes.form.domain.repository.AbstractFormDataRepository;
import com.greenstone.mes.form.dto.cmd.FormDataRemoveCmd;
import com.greenstone.mes.form.dto.cmd.FormDataRevokeCmd;
import com.greenstone.mes.form.dto.cmd.FormDataStatusChangeCmd;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import com.greenstone.mes.system.dto.result.MenuBriefResult;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Slf4j
public abstract class AbstractFormDataService<E extends BaseFormDataEntity, P extends BaseFormPo, M extends BaseMapper<P>> implements BaseFormDataService<E, P, M> {

    private final AbstractFormDataRepository<E, P, M> formDataRepository;
    private final ProcessInstanceService processInstanceService;
    private final FormHelper formHelper;

    public AbstractFormDataService(AbstractFormDataRepository<E, P, M> formDataRepository, ProcessInstanceService processInstanceService,
                                   FormHelper formHelper) {
        this.formDataRepository = formDataRepository;
        this.processInstanceService = processInstanceService;
        this.formHelper = formHelper;
    }

    /**
     * 数据查询操作
     **/

    @Override
    public List<E> query(FormDataQuery query) {
        return formDataRepository.queryEntityList(query);
    }

    @Override
    public <R> List<R> query2Result(FormDataQuery query, Function<List<E>, List<R>> assembler) {
        List<E> entityList = getFormDataRepository().queryEntityList(query);
        return assembler.apply(entityList);
    }

    @Override
    public <R> List<R> queryPo2Result(FormDataQuery query, Function<List<P>, List<R>> assembler) {
        List<P> poList = getFormDataRepository().queryPoList(query);
        return assembler.apply(poList);
    }

    /**
     * 保存草稿
     **/
    public final E saveDraft(E entity) {
        entityExistCheck(entity);
        entity.setStatus(ProcessStatus.DRAFT);
        beforeSaveDraft(entity);
        E e = insertOrUpdateDraft(entity);
        afterSaveDraft(entity);
        return e;
    }


    protected void beforeSaveDraft(E entity) {
    }

    protected E insertOrUpdateDraft(E entity) {
        P p = getFormDataRepository().saveEntity(entity);
        return getFormDataRepository().getEntityById(entity.getFormId(), p.getId());
    }

    protected void afterSaveDraft(E entity) {
    }

    /**
     * 提交
     **/
    public final E saveCommit(E entity) {
        entityExistCheck(entity);
        setStatusBeforeCommit(entity);
        beforeSaveCommit(entity);
        E e = insertOrUpdateCommit(entity);
//        startProcess(entity);
        afterSaveCommit(entity);
        return e;
    }

    private void setStatusBeforeCommit(E entity) {
        entity.setStatus(getFormHelper().usingProcess(entity.getFormId()) ? ProcessStatus.WAIT_APPROVE : ProcessStatus.COMMITTED);
    }

    protected void beforeSaveCommit(E entity) {

    }

    protected E insertOrUpdateCommit(E entity) {
        P p = formDataRepository.saveEntity(entity);
        return formDataRepository.getEntityById(entity.getFormId(), p.getId());
    }

    protected void afterSaveCommit(E entity) {

    }

    /**
     * 状态变更
     */
    public void changeStatus(FormDataStatusChangeCmd statusChangeCmd) {
        formDataRepository.changeStatusBySerialNo(statusChangeCmd.getSerialNo(), statusChangeCmd.getStatus());
    }

    /**
     * 删除
     */
    @GlobalTransactional
    public void delete(FormDataRemoveCmd removeCmd) {
        formDataRepository.deleteByIds(removeCmd.getFormId(), removeCmd.getIds());
        afterDelete(removeCmd);
    }

    protected void afterDelete(FormDataRemoveCmd removeCmd) {

    }

    /**
     * 撤回
     */
    @GlobalTransactional
    public void revoke(FormDataRevokeCmd revokeCmd) {
        formDataRepository.revokeByIds(revokeCmd.getFormId(), revokeCmd.getIds());
        // TODO 需要删除对应的流程
        afterRevoke(revokeCmd);
    }

    protected void afterRevoke(FormDataRevokeCmd revokeCmd) {

    }

    /**
     * 开始流程
     */
    protected void startProcess(E entity) {
        if (formHelper.usingProcess(entity.getFormId())) {
            ProcessStartCmd startCmd = ProcessStartCmd.builder().formId(entity.getFormId()).formName("采购申请").serialNo(entity.getSerialNo()).build();
            processInstanceService.createAndRun(startCmd);
            // TODO 加上流程和实例信息
        }
    }

    /**
     * 批量审批流程
     */
    public void processBatch(ProcessCmd processCmd) {
        ProcessInstanceService instanceService = SpringUtil.getBean(ProcessInstanceService.class);
        List<ProcessRunResult> results = instanceService.runProcess(processCmd);
        Map<String, List<ProcessRunResult>> formIdMap = results.stream().collect(Collectors.groupingBy(ProcessRunResult::getFormId));
        formIdMap.forEach((formId, subResults) -> {
            MenuBriefResult menuInfo = formHelper.getMenuInfo(subResults.get(0).getFormId());
            BaseFormDataService<?, ?, ?> formDataService = formHelper.getFormDataService(menuInfo.getServiceName());
            subResults.forEach(formDataService::handleProcessResult);
        });
    }

    /**
     * 根据审批结果处理表单数据
     */
    public void handleProcessResult(ProcessRunResult processResult) {
        if (processResult.isComplete()) {
            endProcess(processResult);
        }
    }

    private void endProcess(ProcessRunResult processResult) {
        if (processResult.isApproved()) {
            changeStatus(processResult, ProcessStatus.APPROVED);
            afterApproved(processResult);
        } else {
            changeStatus(processResult, ProcessStatus.REJECTED);
            afterRejected(processResult);
        }
    }

    protected void afterApproved(ProcessRunResult processResult) {

    }

    protected void afterRejected(ProcessRunResult processResult) {

    }

    public void changeStatus(ProcessRunResult processResult, ProcessStatus status) {
        BaseFormDataService<?, ?, ?> formDataService = formHelper.getFormDataService(processResult.getServiceName());
        formDataService.changeStatus(FormDataStatusChangeCmd.builder().serialNo(processResult.getSerialNo()).status(status).build());
    }

    private void entityExistCheck(E entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            if (null == formDataRepository.getPoById(entity.getFormId(), entity.getId())) {
                log.error("所选单据不存在，formId: {}，id: {}", entity.getFormId(), entity.getId());
                throw new ServiceException(FormError.E70101);
            }
        }
    }

}
