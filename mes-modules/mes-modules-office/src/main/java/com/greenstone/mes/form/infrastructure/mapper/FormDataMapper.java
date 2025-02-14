package com.greenstone.mes.form.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.form.infrastructure.persistence.FormDataPo;
import org.springframework.stereotype.Repository;

@Repository
public interface FormDataMapper extends EasyBaseMapper<FormDataPo> {
}
