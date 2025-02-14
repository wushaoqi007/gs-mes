package com.greenstone.mes.material.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.web.page.PageList;
import com.greenstone.mes.material.domain.converter.CheckRecordConverter;
import com.greenstone.mes.material.domain.entity.CheckRecord;
import com.greenstone.mes.material.dto.CheckRecordListQuery;
import com.greenstone.mes.material.infrastructure.mapper.CheckRecordMapper;
import com.greenstone.mes.material.infrastructure.persistence.CheckRecordDO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/19 10:28
 */
@Component
public class CheckRecordRepository {

    private CheckRecordMapper checkRecordMapper;
    private CheckRecordConverter checkRecordConverter;

    public CheckRecordRepository(CheckRecordMapper checkRecordMapper, CheckRecordConverter checkRecordConverter) {
        this.checkRecordMapper = checkRecordMapper;
        this.checkRecordConverter = checkRecordConverter;
    }

    public void save(CheckRecord checkRecord) {
        CheckRecordDO checkRecordDO = checkRecordConverter.toCheckRecordDO(checkRecord);
        checkRecordMapper.insert(checkRecordDO);
        checkRecord.setId(checkRecordDO.getId());
    }

    public PageList<CheckRecord> list(CheckRecordListQuery query) {
        CheckRecordDO queryDO = CheckRecordDO.builder().projectCode(query.getProjectCode())
                .sponsor(query.getSponsor())
                .result(query.getResult())
                .materialCode(query.getMaterialCode())
                .ngType(query.getNgType())
                .subNgType(query.getSubNgType()).build();
        LambdaQueryWrapper<CheckRecordDO> queryWrapper = Wrappers.lambdaQuery(queryDO).ge(query.getStartTime() != null, CheckRecordDO::getCreateTime, query.getStartTime())
                .le(query.getEndTime() != null, CheckRecordDO::getCreateTime, query.getEndTime());
        List<CheckRecordDO> checkRecordDOs = checkRecordMapper.selectList(queryWrapper);
        List<CheckRecord> checkRecords = checkRecordConverter.toCheckRecords(checkRecordDOs);
        return PageList.of(checkRecordDOs, checkRecords);
    }

}
