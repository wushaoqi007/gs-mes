package com.greenstone.mes.form.domain.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.greenstone.mes.external.dto.cmd.ProcessCmd;
import com.greenstone.mes.external.dto.result.ProcessRunResult;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.form.domain.BaseFormDataEntity;
import com.greenstone.mes.form.dto.cmd.FormDataRemoveCmd;
import com.greenstone.mes.form.dto.cmd.FormDataRevokeCmd;
import com.greenstone.mes.form.dto.cmd.FormDataStatusChangeCmd;
import com.greenstone.mes.form.dto.query.FormDataQuery;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

public interface BaseFormDataService<E extends BaseFormDataEntity, P extends BaseFormPo, M extends BaseMapper<P>> {

    List<E> query(FormDataQuery query);

    <R> List<R> query2Result(FormDataQuery query, Function<List<E>, List<R>> assembler);

    <R> List<R> queryPo2Result(FormDataQuery query, Function<List<P>, List<R>> assembler);

    @Transactional
    default <SC> E saveDraft(SC saveCmd, Function<SC, E> assembler) {
        E e = assembler.apply(saveCmd);
        return saveDraft(e);
    }

    @Transactional
    E saveDraft(E entity);

    @Transactional
    default <SC> E saveCommit(SC saveCmd, Function<SC, E> assembler) {
        E e = assembler.apply(saveCmd);
        return saveCommit(e);
    }

    @Transactional
    E saveCommit(E entity);

    void changeStatus(FormDataStatusChangeCmd statusChangeCmd);

    void delete(FormDataRemoveCmd removeCmd);

    void revoke(FormDataRevokeCmd revokeCmd);

    void processBatch(ProcessCmd processCmd);

    void handleProcessResult(ProcessRunResult processResult);
}
