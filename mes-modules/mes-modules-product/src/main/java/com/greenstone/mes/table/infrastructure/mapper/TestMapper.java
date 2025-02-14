package com.greenstone.mes.table.infrastructure.mapper;

import com.greenstone.mes.table.infrastructure.config.mubatisplus.TableBaseMapper;
import com.greenstone.mes.table.infrastructure.persistence.TestPo;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMapper extends TableBaseMapper<TestPo> {
}
