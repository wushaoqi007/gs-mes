package com.greenstone.mes.oa.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.oa.dto.result.DormMemberResult;
import com.greenstone.mes.oa.infrastructure.persistence.DormMemberDo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DormMemberMapper extends EasyBaseMapper<DormMemberDo> {

    DormMemberResult selectDormMember(@Param("employeeId") Long employeeId);
}
