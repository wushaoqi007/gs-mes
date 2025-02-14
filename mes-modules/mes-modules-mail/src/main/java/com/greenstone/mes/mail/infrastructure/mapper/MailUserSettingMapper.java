package com.greenstone.mes.mail.infrastructure.mapper;

import com.greenstone.mes.common.mybatisplus.EasyBaseMapper;
import com.greenstone.mes.mail.infrastructure.persistence.MailUserSetting;
import org.springframework.stereotype.Repository;

@Repository
public interface MailUserSettingMapper extends EasyBaseMapper<MailUserSetting> {
}
