package com.greenstone.mes.table.core;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;

import java.util.List;

public interface TableRepository<E extends TableEntity, P extends TablePo, M extends EasyBaseMapper<P>> {

    boolean isEntityExist(E entity);

    E getEntity(Long id);

    List<E> getEntities(E e);

    List<E> getDrafts();

    E insert(E e);

    E update(E e);

    void updateStatus(E e);

    void delete(Long id);

    void batchDelete(List<? extends Long> ids);

    void lock(Long id);

    void unlock(Long id);

    void waste(Long id);

    List<P> getItemsStatus(List<? extends Long> ids);

    void updateApprovalChange(ApprovalChangeMsg msg);

    void updateProcessStartInfo(E e);

}
