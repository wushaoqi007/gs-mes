package com.greenstone.mes.system.dto.query;


import com.greenstone.mes.system.enums.MsgCategory;
import com.greenstone.mes.system.enums.MsgStatus;
import lombok.Data;

@Data
public class MessageListQuery {

    private MsgStatus status;

    private MsgCategory category;

}
