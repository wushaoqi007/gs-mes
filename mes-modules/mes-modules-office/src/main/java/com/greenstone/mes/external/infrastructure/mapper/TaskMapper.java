package com.greenstone.mes.external.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.external.infrastructure.persistence.TaskDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/3/2 9:15
 */
@Repository
public interface TaskMapper extends EasyBaseMapper<TaskDO> {

    List<TaskDO> pendingTasks(@Param("userId") Long userId);

}
