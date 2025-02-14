package com.greenstone.mes.system.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.system.infrastructure.po.MessageDo;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageMapper extends EasyBaseMapper<MessageDo> {

}
