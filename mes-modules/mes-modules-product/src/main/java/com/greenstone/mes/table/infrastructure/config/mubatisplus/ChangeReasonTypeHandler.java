package com.greenstone.mes.table.infrastructure.config.mubatisplus;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.greenstone.mes.table.TableChangeReason;
import com.greenstone.mes.table.core.TableThreadLocal;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings({"unchecked"})
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({TableChangeReason.class})
public class ChangeReasonTypeHandler extends BaseTypeHandler<TableChangeReason> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, TableChangeReason tableChangeReason, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSONObject.toJSONString(tableChangeReason));
    }

    @Override
    public TableChangeReason getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Field field = ReflectUtil.getField(TableThreadLocal.getTableMeta().getEntityClass(), "changeReason");
        return JSONObject.parseObject(resultSet.getString(s), (Class<? extends TableChangeReason>) field.getType());
    }

    @Override
    public TableChangeReason getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Field field = ReflectUtil.getField(TableThreadLocal.getTableMeta().getEntityClass(), "changeReason");
        return JSONObject.parseObject(resultSet.getString(i), (Class<? extends TableChangeReason>) field.getType());
    }

    @Override
    public TableChangeReason getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Field field = ReflectUtil.getField(TableThreadLocal.getTableMeta().getEntityClass(), "changeReason");
        return JSONObject.parseObject(callableStatement.getString(i), (Class<? extends TableChangeReason>) field.getType());
    }
}
