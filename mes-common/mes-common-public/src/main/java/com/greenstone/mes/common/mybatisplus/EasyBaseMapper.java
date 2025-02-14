package com.greenstone.mes.common.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.constant.CommonDbConst;
import com.greenstone.mes.common.core.exception.ServiceException;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Objects;

/**
 * @author gu_renkai
 * @date 2022/12/6 10:58
 */

public interface EasyBaseMapper<T> extends BaseMapper<T> {

    Integer insertBatchSomeColumn(List<T> entityList);

    default T getOneOnly(T entity) {
        QueryWrapper<T> wrapper = Wrappers.query(entity);
        return getOneOnly(wrapper);
    }

    default List<T> list(T entity) {
        return this.selectList(Wrappers.query(entity));
    }

    default Long selectCount(T entity) {
        return this.selectCount(Wrappers.query(entity));
    }

    default T getOneOnly(QueryWrapper<T> wrapper) {
        wrapper.last(CommonDbConst.LIMIT_ONE);
        return this.selectOne(wrapper);
    }

    default boolean exists(T entity) {
        return getOneOnly(entity) != null;
    }

    default int delete(T entity) {
        return this.delete(Wrappers.query(entity));
    }

    default void duplicatedCheck(T entity, String msg) {
        T oneOnly = getOneOnly(entity);
        if (Objects.nonNull(oneOnly)) {
            throw new ServiceException(msg);
        }
    }

    default int update(Wrapper<T> updateWrapper) {
        return this.update(null, updateWrapper);
    }

    int recoverBatchByPk(@Param("list") List<T> batchList);

    int recoverBatch(@Param(Constants.WRAPPER) Wrapper<T> ew);

    default List<T> findWithOutLogic(T t) {
        return selectWithOutLogic(Wrappers.query(t));
    }

    default T findOneWithOutLogic(T t) {
        return findOneWithOutLogic(Wrappers.lambdaQuery(t));
    }

    default T findOneWithOutLogic(AbstractWrapper<T, ?, ?> ew) {
        List<T> list = selectWithOutLogic(ew.last(CommonDbConst.LIMIT_ONE));
        return list == null || list.size() == 0 ? null : list.get(0);
    }

    List<T> selectWithOutLogic(@Param(Constants.WRAPPER) Wrapper<T> ew);

    List<T> selectByDataScope(@Param(Constants.WRAPPER) Wrapper<T> ew);

}