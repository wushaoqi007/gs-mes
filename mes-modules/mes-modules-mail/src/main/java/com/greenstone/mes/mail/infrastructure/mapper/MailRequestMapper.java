package com.greenstone.mes.mail.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRequestMapper extends EasyBaseMapper<MailRequest> {
}
