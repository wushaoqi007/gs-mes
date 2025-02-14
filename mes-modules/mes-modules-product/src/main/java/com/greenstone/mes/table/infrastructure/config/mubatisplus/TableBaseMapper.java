package com.greenstone.mes.table.infrastructure.config.mubatisplus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.table.core.TableThreadLocal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2024/11/1 13:53
 */

public interface TableBaseMapper<T> extends EasyBaseMapper<T> {

    default List<T> selectByDataScopeLambda(@Param(Constants.WRAPPER) LambdaQueryWrapper<T> ew) {
        if (ew == null) {
            ew = new LambdaQueryWrapper<>();
        }
        String dataScopeSql = TableThreadLocal.getTableMeta().getDataScopeSql();
        if (dataScopeSql != null) {
            ew.apply(dataScopeSql);
        }
        return selectByDataScope(ew);
    }

}