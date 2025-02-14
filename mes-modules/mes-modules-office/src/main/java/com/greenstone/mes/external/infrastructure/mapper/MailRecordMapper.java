package com.greenstone.mes.external.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.external.infrastructure.persistence.MailRecordDo;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRecordMapper extends EasyBaseMapper<MailRecordDo> {

}
