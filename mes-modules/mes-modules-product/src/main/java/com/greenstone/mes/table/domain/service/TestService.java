package com.greenstone.mes.table.domain.service;

import com.greenstone.mes.table.core.TableService;
import com.greenstone.mes.table.domain.entity.TestEntity;
import com.greenstone.mes.table.infrastructure.mapper.TestMapper;
import com.greenstone.mes.table.infrastructure.persistence.TestPo;

public interface TestService extends TableService<TestEntity, TestPo, TestMapper> {
}
