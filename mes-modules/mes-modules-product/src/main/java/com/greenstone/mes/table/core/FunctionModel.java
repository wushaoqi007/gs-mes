package com.greenstone.mes.table.core;

import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import lombok.Data;

@Data
public class FunctionModel<E extends TableEntity, P extends TablePo> {

    private Long functionId;

    private String functionName;

    private Boolean usingProcess;

    private String templateId;

    private TableService<E, P, ?> tableService;

    private TableFunction function;

    public Class<E> getEntityClass() {
        return (Class<E>) function.entityClass();
    }

    public Class<P> getPoClass() {
        return (Class<P>) function.poClass();
    }
}
