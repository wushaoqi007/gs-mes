package com.greenstone.mes.system.domain.service;

import com.greenstone.mes.system.application.dto.result.MessageUnreadCountResult;
import com.greenstone.mes.system.dto.cmd.MessageSaveCmd;
import com.greenstone.mes.system.dto.query.MessageListQuery;
import com.greenstone.mes.system.dto.result.MessageListResult;
import com.greenstone.mes.system.dto.result.MessageResult;

import java.util.List;

public interface MessageService {

    MessageUnreadCountResult getUnreadCount();

    MessageResult get(String id);

    List<MessageListResult> list(MessageListQuery query);

    void save(MessageSaveCmd saveCmd);

    void read(List<String> msgIds);

    void delete(List<String> msgIds);

    void readAll();

    void deleteAll();

}
