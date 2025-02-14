package com.greenstone.mes.common.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.greenstone.mes.common.constant.CommonDbConst;
import com.greenstone.mes.common.core.exception.ServiceException;

import java.util.List;
import java.util.Objects;

public interface IMapperWrapper<T> extends BaseMapper<T> {

    default T getOneOnly(T entity) {
        QueryWrapper<T> wrapper = Wrappers.query(entity);
        return getOneOnly(wrapper);
    }

    default T getOneOnly(QueryWrapper<T> wrapper) {
        wrapper.last(CommonDbConst.LIMIT_ONE);
        return this.selectOne(wrapper);
    }

    default void duplicatedCheck(T entity, String msg) {
        T oneOnly = getOneOnly(entity);
        if (Objects.nonNull(oneOnly)) {
            throw new ServiceException(msg);
        }
    }

    default List<T> listPlus(T entity){
        return selectList(WrappersPlus.query(entity));
    }

}
