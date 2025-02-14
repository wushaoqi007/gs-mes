package com.greenstone.mes.table.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.table.*;
import com.greenstone.mes.table.adapter.UserServiceAdapter;
import com.greenstone.mes.table.infrastructure.annotation.ReasonField;
import com.greenstone.mes.table.infrastructure.config.LinkConfig;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import com.greenstone.mes.table.infrastructure.utils.ItemStreamUtil;
import com.greenstone.mes.table.infrastructure.utils.PermissionUtil;
import com.greenstone.mes.table.interfaces.event.ItemCreateEvent;
import com.greenstone.mes.table.interfaces.event.ItemUpdateEvent;
import com.greenstone.mes.table.interfaces.event.ItemWasteEvent;
import com.greenstone.mes.workflow.cmd.FlowCommitCmd;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import com.greenstone.mes.workflow.resp.FlowCommitResp;
import com.greenstone.mes.wxcp.api.RemoteWorkflowService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class AbstractTableService<E extends TableEntity, P extends TablePo, M extends TableBaseMapper<P>> implements TableService<E, P, M> {

    private final TableRepository<E, P, M> tableRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public E getEntity(Long id) {
        return getEntity(id, true);
    }

    @Override
    public E getEntity(Long id, boolean checkPermission) {
        if (checkPermission) {
            PermissionUtil.checkPermission(null, TableConst.Rights.VIEW);
        }
        E entity = tableRepository.getEntity(id);
        if (checkPermission) {
            entity.setEditable(PermissionUtil.editable(entity, TableConst.Rights.UPDATE));
        }
        setEntityUser(entity);
        return entity;
    }

    protected void setEntityUser(E e) {
        UserServiceAdapter userService = SpringUtils.getBean(UserServiceAdapter.class);
        if (e.getCreateBy() != null) {
            e.setCreateUser(userService.getUserById(e.getCreateBy()));
        }
        if (e.getSubmitBy() != null) {
            e.setSubmitUser(userService.getUserById(e.getSubmitBy()));
        }
        if (e.getUpdateBy() != null) {
            e.setUpdateUser(userService.getUserById(e.getUpdateBy()));
        }
        if (e.getProcessStartBy() != null) {
            e.setProcessStartUser(userService.getUserById(e.getProcessStartBy()));
        }
    }

    @Override
    public final List<E> getDrafts() {
        PermissionUtil.checkPermission(null, TableConst.Rights.VIEW);
        List<E> drafts = tableRepository.getDrafts();
        for (E draft : drafts) {
            draft.setEditable(true);
            setEntityUser(draft);
        }
        return drafts;
    }

    @Override
    public final List<E> getEntities(E e) {
        PermissionUtil.checkPermission(null, TableConst.Rights.VIEW);
        List<E> entities = tableRepository.getEntities(e);
        for (E entity : entities) {
            entity.setEditable(PermissionUtil.editable(entity, TableConst.Rights.UPDATE));
            setEntityUser(entity);
        }
        return entities;
    }

    /**
     * 创建数据
     *
     * @param entity 实体对象
     * @return 创建后的实体对象
     */
    @Override
    public E create(E entity) {
        entity.setFunctionId(TableThreadLocal.getTableMeta().getFunctionId());
        PermissionUtil.checkPermission(null, TableConst.Rights.CREATE);
        if (entity.getDataStatus() == null) {
            entity.setDataStatus(TableConst.DataStatus.DRAFT);
        }
        // 如果不是草稿状态的，则锁定数据
        entity.setLocked(TableConst.DataStatus.DRAFT != entity.getDataStatus());
        entity.setSerialNo(generateSerialNo(entity));
        beforeCreate(entity);
        E e = tableRepository.insert(entity);
        if (e.getDataStatus() != TableConst.DataStatus.DRAFT) {
            publishCreateEvent(e);
        }
        afterCreate(e);
        return e;
    }

    /**
     * 更新数据，只要有更新权限且数据处于解锁状态就可以更新
     *
     * @param e 实体对象
     * @return 更新后的实体对象
     */
    @Override
    public E update(E e, E oldE) {
        beforeUpdate(e);

        if (e.getId() == null) {
            throw new RuntimeException("更新失败：未指定需要更新的数据");
        }
        if (oldE == null) {
            oldE = getEntity(e.getId());
        }
        if (oldE == null) {
            throw new RuntimeException("更新失败：数据不存在");
        }

        if (oldE.getDataStatus() != null && TableConst.DataStatus.DRAFT != oldE.getDataStatus()) {
            TableThreadLocal.ActionMeta<TableEntity, TablePo> tableMeta = TableThreadLocal.getTableMeta();
            // 原因必填且有需要记录的变更时，需要填原因
            if (tableMeta.getFunction().updateReason() == UpdateReason.NECESSARY && !ItemStreamUtil.equals(e, oldE)) {
                if (e.getChangeReason() == null) {
                    throw new RuntimeException("更新失败：请填写原因");
                } else {
                    checkNecessaryReason(e);
                }
            }
        }

        e.setFunctionId(TableThreadLocal.getTableMeta().getFunctionId());
        oldE.setFunctionId(TableThreadLocal.getTableMeta().getFunctionId());

        PermissionUtil.checkPermission(oldE, TableConst.Rights.UPDATE);

        // 如果不是草稿状态的更新，则在更新时锁定数据
        if (e.getDataStatus() != null && TableConst.DataStatus.DRAFT != e.getDataStatus() || oldE.getDataStatus() != null && TableConst.DataStatus.DRAFT != oldE.getDataStatus()) {
            e.setLocked(true);
        }
        // 如果数据一致则不需要更新数据，但需要更新状态
//        if (ItemStreamUtil.equals(e, oldE)) {
//            log.info("数据未改变，不需要更新");
//            // 更新状态
//            if (isStatusChange(e, oldE)) {
//                tableRepository.updateStatus(e);
//                log.info("更新数据状态成功");
//            }
//            return e;
//        }

        E entity = tableRepository.update(e);

        // 如果不是草稿状态的更新(且有需要记录的变更)，则记录修改信息
        if (TableConst.DataStatus.DRAFT != oldE.getDataStatus() && !ItemStreamUtil.equals(e, oldE)) {
            publishUpdateEvent(entity, oldE);
        }

        afterUpdate(entity);
        return entity;
    }

    /**
     * 提交数据，如果没有id就先保存再提交，如果有id就先更新再提交
     *
     * @param entity 实体对象
     */
    @Override
    public E submit(E entity) {
        entity.setFunctionId(TableThreadLocal.getTableMeta().getFunctionId());
        entity.setSubmitBy(SecurityUtils.getUserId());
        entity.setSubmitTime(LocalDateTime.now());
        beforeSubmit(entity);
        setStatusBeforeSubmit(entity);
        E e;
        if (entity.getId() == null) {
            e = create(entity);
        } else {
            E oldEntity = getEntity(entity.getId());
            if (oldEntity == null) {
                throw new RuntimeException("提交失败：数据不存在");
            }
            if (oldEntity.getDataStatus() != TableConst.DataStatus.DRAFT) {
                throw new RuntimeException("提交失败：不能重复提交");
            }
            e = update(entity, oldEntity);
        }
        if (haveProcess()) {
            startProcess(e);
        }
        afterSubmit(e);
        return e;
    }

    @Override
    public void lock(Long id) {
        PermissionUtil.checkPermission(null, TableConst.Rights.LOCK);

        List<P> statusList = tableRepository.getItemsStatus(List.of(id));
        if (CollUtil.isEmpty(statusList)) {
            throw new RuntimeException("锁定失败：数据不存在");
        }
        if (statusList.get(0).getLocked()) {
            throw new RuntimeException("锁定失败：数据未解锁");
        }
        tableRepository.lock(id);
    }

    @Override
    public void unlock(Long id) {
        PermissionUtil.checkPermission(null, TableConst.Rights.UNLOCK);

        List<P> statusList = tableRepository.getItemsStatus(List.of(id));
        if (CollUtil.isEmpty(statusList)) {
            throw new RuntimeException("锁定失败：数据不存在");
        }
        if (!statusList.get(0).getLocked()) {
            throw new RuntimeException("解锁失败：数据已解锁");
        }
        tableRepository.unlock(id);
    }

    @Override
    public void delete(Long id) {
        PermissionUtil.checkPermission(null, TableConst.Rights.DELETE);

        List<P> statusList = tableRepository.getItemsStatus(List.of(id));
        if (CollUtil.isEmpty(statusList)) {
            throw new RuntimeException("删除失败：数据不存在");
        }

        E entity = getEntity(id);

        beforeDelete(entity);

        if (statusList.get(0).getDataStatus() == TableConst.DataStatus.DRAFT) {
            tableRepository.delete(id);
        } else {
            if (PermissionUtil.hasManagePermission(null, TableConst.Rights.DELETE)) {
                tableRepository.waste(id);
            } else {
                throw new RuntimeException("删除失败：只能删除草稿");
            }
        }

        afterDelete(entity);

        publishWasteEvent(entity);
    }

    @Override
    public void batchDelete(List<? extends Long> ids) {
        PermissionUtil.checkPermission(null, TableConst.Rights.DELETE);

        List<P> statusList = tableRepository.getItemsStatus(ids);
        if (CollUtil.isEmpty(statusList) || ids.size() != statusList.size()) {
            throw new RuntimeException("删除失败：数据不存在");
        }
        List<E> deleteEntity = new ArrayList<>();

        for (Long id : ids) {
            E entity = getEntity(id);
            beforeDelete(entity);

            if (statusList.get(0).getDataStatus() == TableConst.DataStatus.DRAFT) {
                tableRepository.delete(id);
            } else {
                if (PermissionUtil.hasManagePermission(null, TableConst.Rights.DELETE)) {
                    tableRepository.waste(id);
                } else {
                    throw new RuntimeException("删除失败：只能删除草稿");
                }
            }

            afterDelete(entity);
            deleteEntity.add(entity);
        }

        for (E e : deleteEntity) {
            publishWasteEvent(e);
        }
    }

    @Override
    public String generateSerialNo(E e) {
        return IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    public final SysFile exportData(E e) {
        PermissionUtil.checkPermission(null, TableConst.Rights.EXPORT);

        MultipartFile multipartFile = exportImpl(e);
        RemoteFileService fileService = SpringUtils.getBean(RemoteFileService.class);
        return fileService.upload(multipartFile, 15).getData();

    }

    /**
     * 导出，如果需要支持导出功能，则重写这个方法
     *
     * @param e 查询参数
     * @return 文件
     */
    protected MultipartFile exportImpl(E e) {
        throw new RuntimeException("不支持导出数据");
    }

    /**
     * 导入，如果需要支持导入功能，则重写这个方法
     *
     * @param file   导入文件
     * @param params formdata参数
     */
    @Override
    public void importData(MultipartFile file, Map<String, Object> params) {
        PermissionUtil.checkPermission(null, TableConst.Rights.IMPORT);
        importImpl(file, params);
    }

    /**
     * 导入，如果需要支持导入功能，则重写这个方法
     *
     * @param file   导入文件
     * @param params formdata参数
     */
    protected void importImpl(MultipartFile file, Map<String, Object> params) {
        throw new RuntimeException("不支持导出数据");
    }

    @Override
    public void updateApprovalChange(ApprovalChangeMsg msg) {
        tableRepository.updateApprovalChange(msg);
    }

    protected void beforeCreate(E entity) {
    }

    protected void afterCreate(E entity) {
    }

    protected void beforeUpdate(E entity) {
    }

    protected void afterUpdate(E entity) {
    }

    protected void beforeSubmit(E entity) {
    }

    protected void afterSubmit(E entity) {
    }

    protected void beforeDelete(E entity) {
    }

    protected void afterDelete(E entity) {
    }

    protected void beforeStartProcess(E entity) {
    }

    protected void afterStartProcess(E entity) {
    }

    private void startProcess(E entity) {
        beforeStartProcess(entity);
        TableThreadLocal.ActionMeta<TableEntity, TablePo> tableMeta = TableThreadLocal.getTableMeta();

        FlowCommitCmd commitCmd = FlowCommitCmd.builder().businessKey(String.valueOf(tableMeta.getFunctionId()))
                .applyUserId(SecurityUtils.getUserId()).build();
        List<FlowCommitCmd.Attr> attrs = new ArrayList<>();
        commitCmd.setAttrs(attrs);
        Field[] fields = ReflectUtil.getFields(entity.getClass());
        for (Field field : fields) {
            WorkflowField workflowField = field.getDeclaredAnnotation(WorkflowField.class);
            if (workflowField != null) {
                String fieldName = StrUtil.isBlank(workflowField.value()) ? field.getName() : workflowField.value();
                Object fieldValue = ReflectUtil.getFieldValue(entity, field);
                String value;
                if (workflowField.fieldType() == FlowFieldType.DETAIL_LINK) {
                    LinkConfig linkConfig = SpringUtils.getBean(LinkConfig.class);
                    value = linkConfig.getDetailLink(tableMeta.getFunctionId(), entity.getId());
                } else {
                    value = getFlowValue(fieldValue, field);
                }
                attrs.add(FlowCommitCmd.Attr.builder().name(fieldName).value(value).build());
            }
        }

        try {
            log.debug("提交企业微信审批: {}", commitCmd);
            RemoteWorkflowService workflowService = SpringUtils.getBean(RemoteWorkflowService.class);
            FlowCommitResp commitResp = workflowService.commit(commitCmd);
            entity.setProcessInstanceNo(commitResp.getInstanceNo());
            entity.setProcessStartBy(SecurityUtils.getUserId());
            entity.setProcessStartTime(LocalDateTime.now());
            tableRepository.updateProcessStartInfo(entity);
            log.info("审批提交成功, 审批编号: " + commitResp.getInstanceNo());
        } catch (Exception e) {
            throw new RuntimeException("提交企业微信审批失败", e);
        }
        afterStartProcess(entity);
    }

    private String getFlowValue(Object o, Field field) {
        if (o instanceof String s) {
            return s;
        } else if (o instanceof Iterable<?> iterable) {
            return CollUtil.join(iterable, ",");
        } else if (o instanceof LocalDateTime dateTime) {
            JsonFormat format = field.getDeclaredAnnotation(JsonFormat.class);
            return LocalDateTimeUtil.format(dateTime, StrUtil.isBlank(format.pattern()) ? "yyyy-MM-dd HH:mm:ss" : format.pattern());
        } else if (o instanceof LocalDate date) {
            JsonFormat format = field.getDeclaredAnnotation(JsonFormat.class);
            return LocalDateTimeUtil.format(date, StrUtil.isBlank(format.pattern()) ? "yyyy-MM-dd" : format.pattern());
        } else {
            return String.valueOf(o);
        }
    }

    private void setStatusBeforeSubmit(E entity) {
        if (haveProcess()) {
            entity.setDataStatus(TableConst.DataStatus.COMMITTED);
            entity.setProcessStatus(TableConst.ProcessStatus.EXECUTING);
        } else {
            entity.setDataStatus(TableConst.DataStatus.EFFECTIVE);
        }
    }

    private boolean haveProcess() {
        return TableThreadLocal.getTableMeta().getUsingProcess();
    }

    private boolean isStatusChange(E e, E oldE) {
        if (oldE.getLocked() != null && !oldE.getLocked().equals(e.getLocked())) {
            return true;
        }
        if (oldE.getDataStatus() != null && !oldE.getDataStatus().equals(e.getDataStatus())) {
            return true;
        }
        if (oldE.getProcessStatus() != null && !oldE.getProcessStatus().equals(e.getProcessStatus())) {
            return true;
        }
        return false;
    }

    private void publishCreateEvent(E entity) {
        ItemCreateEvent<?, ?> createEvent = ItemCreateEvent.builder().item(entity)
                .tableMeta(TableThreadLocal.getTableMeta())
                .operator(SecurityUtils.getLoginUser().getUser()).build();
        eventPublisher.publishEvent(createEvent);
    }

    private void publishUpdateEvent(E entity, E oldEntity) {
        ItemUpdateEvent<?, ?> updateEvent = ItemUpdateEvent.builder().item(entity)
                .oldItem(oldEntity)
                .tableMeta(TableThreadLocal.getTableMeta())
                .operator(SecurityUtils.getLoginUser().getUser()).build();
        eventPublisher.publishEvent(updateEvent);
    }

    private void publishWasteEvent(E entity) {
        ItemWasteEvent<?, ?> wasteEvent = ItemWasteEvent.builder().item(entity)
                .tableMeta(TableThreadLocal.getTableMeta())
                .operator(SecurityUtils.getLoginUser().getUser()).build();
        eventPublisher.publishEvent(wasteEvent);
    }

    private void checkNecessaryReason(E e) {
        TableChangeReason reason = e.getChangeReason();
        Class<? extends TableChangeReason> clazz = reason.getClass();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            ReasonField reasonField = field.getDeclaredAnnotation(ReasonField.class);
            if (reasonField != null && reasonField.necessary() && ReflectUtil.getFieldValue(e.getChangeReason(), field) == null) {
                throw new RuntimeException("更新失败：请填写理由");
            }
        }
    }

}