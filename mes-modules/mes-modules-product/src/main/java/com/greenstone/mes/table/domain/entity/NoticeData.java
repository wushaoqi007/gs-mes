package com.greenstone.mes.table.domain.entity;

import com.greenstone.mes.system.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NoticeData {

    private Long functionId;

    private String functionName;

    private Long itemId;

    private String serialNo;

    private String itemAction;

    private User actionUser;

    private List<Long> emailSendUserIds;

    private List<Long> emailCopyUserIds;

    private List<Long> wxMsgUserIds;

}
