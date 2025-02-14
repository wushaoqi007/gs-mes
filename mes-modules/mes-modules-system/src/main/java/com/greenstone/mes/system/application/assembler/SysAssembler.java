package com.greenstone.mes.system.application.assembler;

import com.greenstone.mes.system.domain.entity.Message;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.result.MessageResult;
import com.greenstone.mes.system.enums.MsgStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/1/31 16:10
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SysAssembler {

    // Message
    default List<Message> fromMessageSaveCmd(MessageSaveCmd saveCmd) {
        List<Message> messages = new ArrayList<>();
        for (Long recipientId : saveCmd.getRecipientIds()) {
            Message message = Message.builder().recipientId(recipientId)
                    .category(saveCmd.getCategory())
                    .sourceId(saveCmd.getSourceId())
                    .status(MsgStatus.NEW)
                    .title(saveCmd.getTitle())
                    .subTitle(saveCmd.getSubTitle())
                    .content(saveCmd.getContent())
                    .build();
            messages.add(message);
        }
        return messages;
    }

}
