package com.greenstone.mes.table.core;

import com.greenstone.mes.common.core.utils.SpringUtils;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.TablePo;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class TableThreadLocal<E extends TableEntity, P extends TablePo> {

    private final ThreadLocal<ActionMeta<E, P>> threadLocal = new ThreadLocal<>();

    public void set(FunctionModel<E, P> model) {
        ActionMeta<E, P> tableMeta = new ActionMeta<>();
        tableMeta.setFunctionId(model.getFunctionId());
        tableMeta.setFunctionName(model.getFunctionName());
        tableMeta.setEntityClass(model.getEntityClass());
        tableMeta.setPoClass(model.getPoClass());
        tableMeta.setFunction(model.getFunction());
        tableMeta.setUsingProcess(model.getUsingProcess());
        threadLocal.set(tableMeta);
    }

    public ActionMeta<E, P> get() {
        return threadLocal.get();
    }

    public void clear() {
        threadLocal.remove();
    }

    public static <E extends TableEntity, P extends TablePo> ActionMeta<E, P> getTableMeta() {
        return SpringUtils.getBean(TableThreadLocal.class).get();
    }

    @Data
    public static class ActionMeta<E extends TableEntity, P extends TablePo> {
        /**
         * 操作字符串 create、update等
         */
        private String action;

        private Long functionId;

        private String functionName;

        private Class<?> entityClass;

        private Class<P> poClass;

        private Boolean usingProcess;

        private TableFunction function;
        /**
         * 数据权限的sql
         */
        private String dataScopeSql;

    }
}
