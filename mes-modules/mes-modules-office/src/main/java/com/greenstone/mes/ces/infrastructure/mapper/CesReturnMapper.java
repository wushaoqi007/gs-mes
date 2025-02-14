package com.greenstone.mes.ces.infrastructure.mapper;

import com.greenstone.mes.ces.application.dto.query.CesReturnFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.CesReturnItemResult;
import com.greenstone.mes.ces.infrastructure.persistence.CesReturnDO;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CesReturnMapper extends EasyBaseMapper<CesReturnDO> {

    List<CesReturnItemResult> listItem(CesReturnFuzzyQuery query);

}
