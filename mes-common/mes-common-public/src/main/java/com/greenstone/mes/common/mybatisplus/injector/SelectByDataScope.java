package com.greenstone.mes.common.mybatisplus.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class SelectByDataScope extends AbstractMethod {

    protected SelectByDataScope() {
        super("selectByDataScope");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        final String sql = "<script>select %s from %s %s</script>";
        final String fieldSql = prepareFieldSql(tableInfo);
        final String whereSql = prepareWhereSql(tableInfo);
        final String sqlResult = String.format(sql, fieldSql, tableInfo.getTableName(), whereSql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, sqlSource, tableInfo);
    }

    private String prepareFieldSql(TableInfo tableInfo) {
        return tableInfo.getAllSqlSelect();
    }

    private String prepareWhereSql(TableInfo tableInfo) {
        return " ${ew.customSqlSegment}";
    }

}
