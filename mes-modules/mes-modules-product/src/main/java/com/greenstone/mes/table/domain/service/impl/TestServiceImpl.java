package com.greenstone.mes.table.domain.service.impl;

import com.greenstone.mes.table.core.AbstractTableService;
import com.greenstone.mes.table.core.TableRepository;
import com.greenstone.mes.table.domain.entity.TestEntity;
import com.greenstone.mes.table.domain.entity.TestChangeReason;
import com.greenstone.mes.table.domain.service.TestService;
import com.greenstone.mes.table.infrastructure.annotation.TableFunction;
import com.greenstone.mes.table.infrastructure.constant.UpdateReason;
import com.greenstone.mes.table.infrastructure.mapper.TestMapper;
import com.greenstone.mes.table.infrastructure.persistence.TestPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@TableFunction(id = "123456",
        entityClass = TestEntity.class,
        poClass = TestPo.class,
        updateReason = UpdateReason.NECESSARY,
        reasonClass = TestChangeReason.class)
@Service
public class TestServiceImpl extends AbstractTableService<TestEntity, TestPo, TestMapper> implements TestService {

    @Autowired
    public TestServiceImpl(TableRepository<TestEntity, TestPo, TestMapper> tableRepository, ApplicationEventPublisher eventPublisher) {
        super(tableRepository, eventPublisher);
    }

}
