package com.greenstone.mes.table.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.table.core.AbstractTableRepository;
import com.greenstone.mes.table.domain.converter.TestConverter;
import com.greenstone.mes.table.domain.entity.TestEntity;
import com.greenstone.mes.table.infrastructure.constant.TableConst;
import com.greenstone.mes.table.infrastructure.mapper.TestMapper;
import com.greenstone.mes.table.infrastructure.persistence.TestPo;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class TestRepository extends AbstractTableRepository<TestEntity, TestPo, TestMapper> {

    private final TestConverter testConverter;

    public TestRepository(TestMapper mapper, TestConverter testConverter) {
        super(mapper);
        this.testConverter = testConverter;
    }

    @Override
    public TestEntity getEntity(Long id) {
        return testConverter.toTest(mapper.selectById(id));
    }

    @Override
    public List<TestEntity> getEntities(TestEntity test) {

        return testConverter.toTests(mapper.selectByDataScopeLambda(null));
    }

    @Override
    public List<TestEntity> getDrafts() {
        QueryWrapper<TestPo> query = Wrappers.query();
        query.eq("data_status", TableConst.DataStatus.DRAFT);
        query.eq("create_by", SecurityUtils.getUserId());
        return testConverter.toTests(mapper.selectList(query));
    }

    @Override
    public TestEntity insert(TestEntity test) {
        TestPo testPo = testConverter.toTestPo(test);
        mapper.insert(testPo);
        return getEntity(testPo.getId());
    }

    @Override
    public TestEntity update(TestEntity test) {
        TestPo testPo = testConverter.toTestPo(test);
        mapper.updateById(testPo);
        return test;
    }

}
