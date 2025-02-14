package com.greenstone.mes.external.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.external.application.dto.result.ProcessCopyResult;
import com.greenstone.mes.external.infrastructure.persistence.ProcessCopyDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessCopyMapper extends EasyBaseMapper<ProcessCopyDO> {

    List<ProcessCopyResult> currUserCopies(@Param("userId") Long userId);

}
