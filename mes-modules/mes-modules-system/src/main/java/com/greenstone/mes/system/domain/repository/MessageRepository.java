package com.greenstone.mes.system.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.system.application.dto.result.MessageUnreadCountResult;
import com.greenstone.mes.system.domain.converter.SysConverter;
import com.greenstone.mes.system.domain.entity.Message;
import com.greenstone.mes.system.dto.query.MessageListQuery;
import com.greenstone.mes.system.dto.result.MessageListResult;
import com.greenstone.mes.system.dto.result.MessageResult;
import com.greenstone.mes.system.enums.MsgStatus;
import com.greenstone.mes.system.infrastructure.mapper.MessageMapper;
import com.greenstone.mes.system.infrastructure.po.MessageDo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class MessageRepository {

    private final MessageMapper messageMapper;
    private final SysConverter converter;

    public MessageUnreadCountResult getUnreadCount(Long userId){
        Long total = messageMapper.selectCount(MessageDo.builder().recipientId(userId).status(MsgStatus.NEW).build());
        return MessageUnreadCountResult.builder().total(total).build();
    }

    public MessageResult get(String id) {
        MessageDo messageDo = messageMapper.selectById(id);
        if (messageDo.getStatus() == MsgStatus.NEW){
            messageMapper.updateById(MessageDo.builder().id(messageDo.getId()).status(MsgStatus.READ).build());
        }
        return converter.toMessageResult(messageDo);
    }

    public List<MessageListResult> list(MessageListQuery query, Long userId) {
        LambdaQueryWrapper<MessageDo> wrapper = Wrappers.lambdaQuery(MessageDo.class)
                .select(MessageDo::getId, MessageDo::getStatus, MessageDo::getCategory, MessageDo::getCreateTime, MessageDo::getTitle, MessageDo::getSubTitle)
                .eq(query.getStatus() != null, MessageDo::getStatus, query.getStatus())
                .eq(query.getCategory() != null, MessageDo::getCategory, query.getCategory())
                .eq(MessageDo::getRecipientId, userId)
                .orderByDesc(MessageDo::getCreateTime);
        List<MessageDo> messageDoList = messageMapper.selectList(wrapper);
        return converter.toMessageListResults(messageDoList);
    }

    public void save(List<Message> messages) {
        List<MessageDo> messageDoList = converter.toMessageDoList(messages);
        messageMapper.insertBatchSomeColumn(messageDoList);
    }

    public void read(List<String> messageIds) {
        LambdaUpdateWrapper<MessageDo> wrapper = Wrappers.lambdaUpdate(MessageDo.class).in(MessageDo::getId, messageIds);
        MessageDo messageDo = MessageDo.builder().status(MsgStatus.READ).build();
        messageMapper.update(messageDo, wrapper);
    }

    public void delete(List<String> messageIds) {
        messageMapper.deleteBatchIds(messageIds);
    }

    public void readAll(Long userId) {
        LambdaUpdateWrapper<MessageDo> wrapper = Wrappers.lambdaUpdate(MessageDo.class).eq(MessageDo::getRecipientId, userId);
        MessageDo messageDo = MessageDo.builder().status(MsgStatus.READ).build();
        messageMapper.update(messageDo, wrapper);
    }

    public void deleteAll(Long userId) {
        LambdaQueryWrapper<MessageDo> wrapper = Wrappers.lambdaQuery(MessageDo.class).eq(MessageDo::getRecipientId, userId);
        messageMapper.delete(wrapper);
    }
}
