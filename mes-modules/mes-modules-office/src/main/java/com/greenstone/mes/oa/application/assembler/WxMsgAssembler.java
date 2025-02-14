package com.greenstone.mes.oa.application.assembler;


import cn.hutool.core.collection.CollUtil;
import com.greenstone.mes.oa.domain.converter.BaseTypeConverter;
import com.greenstone.mes.oa.domain.entity.WxMessage;
import com.greenstone.mes.oa.domain.entity.WxMessageUser;
import com.greenstone.mes.oa.request.WxMsgSendCmd;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {BaseTypeConverter.class},
        imports = {Date.class}
)
public interface WxMsgAssembler {

    default WxMessage toWxMessage(WxMsgSendCmd wxMsgSendCmd) {
        List<WxMessageUser> toUsers = new ArrayList<>();
        WxMessage wxMessage = WxMessage.builder().title(wxMsgSendCmd.getTitle()).content(wxMsgSendCmd.getContent())
                .url(wxMsgSendCmd.getUrl()).cpId(wxMsgSendCmd.getCpId()).toUser(toUsers)
                .agentId(wxMsgSendCmd.getAgentId()).msgType(wxMsgSendCmd.getMsgType()).build();
        if (CollUtil.isNotEmpty(wxMsgSendCmd.getToUser())) {
            List<WxMessageUser> toUser = wxMsgSendCmd.getToUser().stream().map(u -> WxMessageUser.builder().wxUserId(u.getWxUserId()).sysUserId(u.getSysUserId()).build()).collect(Collectors.toList());
            toUsers.addAll(toUser);
        }
        return wxMessage;
    }

}
