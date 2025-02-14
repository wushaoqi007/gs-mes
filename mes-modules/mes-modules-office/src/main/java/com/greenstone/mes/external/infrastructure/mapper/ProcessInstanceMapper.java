package com.greenstone.mes.external.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.external.infrastructure.persistence.ProcessInstanceDO;
import org.springframework.stereotype.Repository;

/**
 * @author gu_renkai
 * @date 2023/3/2 13:37
 */
@Repository
public interface ProcessInstanceMapper extends EasyBaseMapper<ProcessInstanceDO> {
}
