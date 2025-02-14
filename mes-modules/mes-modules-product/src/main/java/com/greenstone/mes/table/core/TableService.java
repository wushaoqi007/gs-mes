package com.greenstone.mes.table.core;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.workflow.mq.ApprovalChangeMsg;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface TableService<E extends TableEntity, P extends TablePo, M extends EasyBaseMapper<P>> {

    List<E> getDrafts();

    E getEntity(Long id);

    E getEntity(Long id, boolean checkPermission);

    List<E> getEntities(E e);

    E create(E e);

    E update(E e, E oldE);

    E submit(E entity);

    void delete(Long id);

    void batchDelete(List<? extends Long> ids);

    void lock(Long id);

    void unlock(Long id);

    String generateSerialNo(E e);

    SysFile exportData(E e);

    void importData(MultipartFile file, Map<String, Object> params);

    void updateApprovalChange(ApprovalChangeMsg msg);

}
