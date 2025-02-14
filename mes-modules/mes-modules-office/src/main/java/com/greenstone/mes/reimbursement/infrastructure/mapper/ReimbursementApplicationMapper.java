package com.greenstone.mes.reimbursement.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.reimbursement.infrastructure.persistence.ReimbursementAppDO;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimbursementApplicationMapper extends EasyBaseMapper<ReimbursementAppDO> {
}
