package com.greenstone.mes.ces.infrastructure.mapper;

import com.greenstone.mes.ces.application.dto.query.RequisitionFuzzyQuery;
import com.greenstone.mes.ces.application.dto.result.RequisitionItemResult;
import com.greenstone.mes.ces.infrastructure.persistence.RequisitionDO;
import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionMapper extends EasyBaseMapper<RequisitionDO> {

    List<RequisitionItemResult> listItem(RequisitionFuzzyQuery query);

}
