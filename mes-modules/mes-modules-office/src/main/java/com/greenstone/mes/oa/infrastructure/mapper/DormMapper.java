package com.greenstone.mes.oa.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.oa.dto.query.DormListQuery;
import com.greenstone.mes.oa.infrastructure.persistence.DormDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DormMapper extends EasyBaseMapper<DormDo> {

    List<DormDo> selectDormList(@Param("query") DormListQuery query);

}
