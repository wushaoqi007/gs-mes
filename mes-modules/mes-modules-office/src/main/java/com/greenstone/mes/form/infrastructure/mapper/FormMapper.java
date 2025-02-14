package com.greenstone.mes.form.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.form.infrastructure.persistence.FormDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/3/6 9:41
 */
@Repository
public interface FormMapper extends EasyBaseMapper<FormDo> {

    boolean createFormDataTable(@Param("tableName") String tableName, @Param("comment") String comment);

    boolean updateFormDataTableComment(@Param("tableName") String tableName, @Param("comment") String comment);

    boolean dropFormDataTable(@Param("tableName") String tableName);

}
