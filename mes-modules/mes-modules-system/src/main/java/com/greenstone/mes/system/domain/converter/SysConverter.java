package com.greenstone.mes.system.domain.converter;

import com.greenstone.mes.system.application.dto.cmd.PermAddCmd;
import com.greenstone.mes.system.application.dto.result.MenuTree;
import com.greenstone.mes.system.application.dto.result.PermTree;
import com.greenstone.mes.system.domain.entity.Message;
import com.greenstone.mes.system.dto.cmd.MenuAddCmd;
import com.greenstone.mes.system.dto.cmd.MenuEditCmd;
import com.greenstone.mes.system.dto.result.FormDefinitionVo;
import com.greenstone.mes.system.dto.result.MessageListResult;
import com.greenstone.mes.system.dto.result.MessageResult;
import com.greenstone.mes.system.infrastructure.po.MenuPo;
import com.greenstone.mes.system.infrastructure.po.MessageDo;
import com.greenstone.mes.system.infrastructure.po.PermPo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/23 11:08
 */
@Repository
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SysConverter {

    // Message
    MessageDo toMessageDo(Message message);

    List<MessageDo> toMessageDoList(List<Message> messages);

    Message toMessage(MessageDo messageDo);

    MessageResult toMessageResult(MessageDo messageDo);

    MessageListResult toMessageListResult(MessageDo messageDo);

    List<MessageListResult> toMessageListResults(List<MessageDo> messageDo);

    MenuPo toMenuPo(MenuAddCmd addCmd);

    List<MenuTree> toMenuTrees(List<MenuPo> menuPoList);

    MenuPo toMenuPo(MenuEditCmd editCmd);

    PermPo toPermPo(PermAddCmd permAddCmd);

    List<PermTree> toPermTrees(List<PermPo> permPoList);

    @Mapping(target = "formId", source = "menuId")
    @Mapping(target = "formName", source = "menuName")
    FormDefinitionVo toFormDefinitionVo(MenuPo menuPo);
}
