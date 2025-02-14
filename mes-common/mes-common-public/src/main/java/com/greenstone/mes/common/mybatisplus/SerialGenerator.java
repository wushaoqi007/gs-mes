package com.greenstone.mes.common.mybatisplus;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Primary
@Component
public class SerialGenerator extends BaseTypeHandler<Serializable> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Serializable serializable, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, String.valueOf(serializable));
    }

    @Override
    public Serializable getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return resultSet.getString(s);
    }

    @Override
    public Serializable getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    @Override
    public Serializable getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }


}
