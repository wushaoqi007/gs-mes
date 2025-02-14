package com.greenstone.mes.system.domain.service.impl;

import com.greenstone.mes.system.application.assembler.SysAssembler;
import com.greenstone.mes.common.security.utils.SecurityUtils;
import com.greenstone.mes.system.application.dto.result.MessageUnreadCountResult;
import com.greenstone.mes.system.domain.entity.Message;
import com.greenstone.mes.system.domain.repository.MessageRepository;
import com.greenstone.mes.system.domain.service.MessageService;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.query.MessageListQuery;
import com.greenstone.mes.system.dto.result.MessageListResult;
import com.greenstone.mes.system.dto.result.MessageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final SysAssembler assembler;
    private final MessageRepository messageRepository;

    @Override
    public MessageUnreadCountResult getUnreadCount() {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return messageRepository.getUnreadCount(userId);
    }

    @Override
    public MessageResult get(String id) {
        return messageRepository.get(id);
    }

    @Override
    public List<MessageListResult> list(MessageListQuery query) {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        return messageRepository.list(query, userId);
    }

    @Override
    public void save(MessageSaveCmd saveCmd) {
        List<Message> messages = assembler.fromMessageSaveCmd(saveCmd);
        messageRepository.save(messages);
    }

    @Override
    public void read(List<String> msgIds) {
        messageRepository.read(msgIds);
    }

    @Override
    public void delete(List<String> msgIds) {
        messageRepository.delete(msgIds);
    }

    @Override
    public void readAll() {
        messageRepository.readAll(SecurityUtils.getLoginUser().getUser().getUserId());
    }

    @Override
    public void deleteAll() {
        messageRepository.deleteAll(SecurityUtils.getLoginUser().getUser().getUserId());
    }

}
