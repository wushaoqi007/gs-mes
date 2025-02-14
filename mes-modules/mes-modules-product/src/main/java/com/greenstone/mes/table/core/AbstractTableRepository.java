package com.greenstone.mes.table.core;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.greenstone.mes.common.core.enums.FormError;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class AbstractTableRepository<E extends TableEntity, P extends TablePo, M extends TableBaseMapper<P>> implements TableRepository<E, P, M> {

    protected final M mapper;

    @Override
    public boolean isEntityExist(E entity) {
        if (entity.getId() == null) {
            return false;
        } else if (null == mapper.selectById(entity.getId())) {
            log.error("单据不存在，请联系管理员，id：" + entity.getId());
            throw new ServiceException(FormError.E70101);
        }
        return true;
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateStatus(E e) {
        if (e.getLocked() == null && e.getDataStatus() == null && e.getProcessStatus() == null) {
            return;
        }
        UpdateWrapper<P> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", e.getId());
        wrapper.set(e.getLocked() != null, "locked", e.getLocked());
        wrapper.set(e.getDataStatus() != null, "data_status", e.getDataStatus());
        wrapper.set(e.getProcessStatus() != null, "process_status", e.getProcessStatus());
        mapper.update(wrapper);
    }

    @Override
    public void batchDelete(List<? extends Long> ids) {
        mapper.deleteBatchIds(ids);
    }

    @Override
    public final void lock(Long id) {
        UpdateWrapper<P> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("locked", true);
        mapper.update(updateWrapper);
    }

    @Override
    public final void unlock(Long id) {
        UpdateWrapper<P> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("locked", false);
        mapper.update(updateWrapper);
    }

    @Override
    public void waste(Long id) {
        UpdateWrapper<P> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("data_status", TableConst.DataStatus.WASTE);
        mapper.update(updateWrapper);
    }

    @Override
    public final List<P> getItemsStatus(List<? extends Long> ids) {
        QueryWrapper<P> w = new QueryWrapper<>();
        w.select("id", "data_status", "locked", "process_status");
        w.in("id", ids);
        return mapper.selectList(w);
    }

    @Override
    public void updateApprovalChange(ApprovalChangeMsg msg) {
        Class<P> poClass = SpringUtils.getBean(TableThreadLocal.class).get().getPoClass();

        P tablePo;
        try {
            tablePo = poClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.debug("内部错误，无法实例化持久化对象");
            throw new RuntimeException("服务器内部错误，请联系管理员");
        }

        tablePo.setId(msg.getItemId());

        int processStatus = getProcessStatus(msg.getStatus());
        int dataStatus = getDataStatus(msg.getStatus());

        tablePo.setProcessStatus(processStatus);
        tablePo.setDataStatus(dataStatus);

        String processProgress = buildProcessProgress(msg, processStatus);
        tablePo.setProcessProgress(processProgress);

        if (TableConst.ProcessStatus.EXECUTING != processStatus) {
            tablePo.setProcessEndTime(LocalDateTime.now());
        }

        mapper.updateById(tablePo);
    }

    @Override
    public void updateProcessStartInfo(E e) {
        UpdateWrapper<P> wrapper = new UpdateWrapper<>();
        wrapper.set("process_instance_no", e.getProcessInstanceNo());
        wrapper.set("process_start_by", e.getProcessStartBy());
        wrapper.set("process_start_time", e.getProcessStartTime());
        wrapper.eq("id", e.getId());
        mapper.update(wrapper);
    }

    private int getDataStatus(ProcessStatus status) {
        return switch (status) {
            case COMMITTED, WAIT_APPROVE, APPROVING -> TableConst.DataStatus.COMMITTED;
            case REJECTED, REVOKED -> TableConst.DataStatus.WASTE;
            case FINISH, APPROVED -> TableConst.DataStatus.EFFECTIVE;
            default -> throw new IllegalStateException("不支持的流程状态：" + status.getName());
        };
    }

    private int getProcessStatus(ProcessStatus status) {
        return switch (status) {
            case COMMITTED, WAIT_APPROVE, APPROVING -> TableConst.ProcessStatus.EXECUTING;
            case REJECTED -> TableConst.ProcessStatus.REJECTED;
            case REVOKED -> TableConst.ProcessStatus.REVOKED;
            case FINISH, APPROVED -> TableConst.ProcessStatus.FINISHED;
            default -> throw new IllegalStateException("不支持的流程状态：" + status.getName());
        };
    }

    private String buildProcessProgress(ApprovalChangeMsg msg, int processStatus) {
        return switch (processStatus) {
            case TableConst.ProcessStatus.EXECUTING -> StrUtil.format("{}：{}({}) 执行中", msg.getNodeName(), msg.getOperator().getNickName(),
                    msg.getOperator().getEmployeeNo());
            case TableConst.ProcessStatus.FINISHED -> StrUtil.format("{}：{}({}) 同意", msg.getNodeName(), msg.getOperator().getNickName(),
                    msg.getOperator().getEmployeeNo());
            case TableConst.ProcessStatus.REVOKED ->
                    StrUtil.format("{}({}) 撤回了申请", msg.getOperator().getNickName(), msg.getOperator().getEmployeeNo());
            case TableConst.ProcessStatus.REJECTED ->
                    StrUtil.format("{}：{}({}) 驳回", msg.getNodeName(), msg.getOperator().getNickName(), msg.getOperator().getEmployeeNo());
            default -> throw new IllegalStateException("不支持的流程状态：" + processStatus);
        };
    }

}
