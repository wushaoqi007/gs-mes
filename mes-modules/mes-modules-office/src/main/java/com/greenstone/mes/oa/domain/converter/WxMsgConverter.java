package com.greenstone.mes.oa.domain.converter;

import com.greenstone.mes.oa.domain.entity.WxMessage;
import com.greenstone.mes.oa.domain.entity.WxMessageUser;
import com.greenstone.mes.oa.infrastructure.persistence.WxMessageDO;
import com.greenstone.mes.oa.infrastructure.persistence.WxMessageUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class}
)
public interface WxMsgConverter {

    WxMessageDO toMessageDO(WxMessage wxMessage);

    default List<WxMessageUserDO> toMessageUserDOS(Long messageId, WxMessage wxMessage) {
        List<WxMessageUserDO> messageUserDOList = new ArrayList<>();
        for (WxMessageUser wxMessageUser : wxMessage.getToUser()) {
            WxMessageUserDO wxMessageUserDO = WxMessageUserDO.builder().messageId(messageId)
                    .wxUserId(wxMessageUser.getWxUserId()).sysUserId(wxMessageUser.getSysUserId()).build();
            messageUserDOList.add(wxMessageUserDO);
        }
        return messageUserDOList;
    }


}
