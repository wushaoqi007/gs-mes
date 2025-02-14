package com.greenstone.mes.common.mybatisplus.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class SelectWithOutLogic extends AbstractMethod {

    protected SelectWithOutLogic() {
        super("selectWithOutLogic");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        if (tableInfo.getLogicDeleteFieldInfo() == null) {
            return null;
        }
        final String sql = "<script>select %s from %s %s</script>";
        final String fieldSql = prepareFieldSql(tableInfo);
        final String whereSql = prepareWhereSqlForMysqlBatch(tableInfo);
        final String sqlResult = String.format(sql, fieldSql, tableInfo.getTableName(), whereSql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, sqlSource, tableInfo);
    }

    private String prepareFieldSql(TableInfo tableInfo) {
        return tableInfo.getAllSqlSelect();
    }

    private String prepareWhereSqlForMysqlBatch(TableInfo tableInfo) {
        return "${ew.customSqlSegment}";
    }

}
