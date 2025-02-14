package com.greenstone.mes.oa.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.oa.dto.query.DormRecordQuery;
import com.greenstone.mes.oa.dto.result.DormRecordResult;
import com.greenstone.mes.oa.infrastructure.persistence.DormRecordDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormRecordMapper extends EasyBaseMapper<DormRecordDo> {


    List<DormRecordResult> selectDormRecords(@Param("query") DormRecordQuery query);

}
