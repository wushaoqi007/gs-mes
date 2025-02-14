package com.greenstone.mes.wxcp.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wxcp_msg")
public class WxcpMsgPo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String agentId;

    private String msgType;

    private String event;

    private String eventKey;

    private String spNo;

    private String fromUserName;

    private String toUserName;

    private LocalDateTime createTime;

    private LocalDateTime receiveTime;

    private String msgId;

    private String topic;

    private String sendStatus;

    private String msgContent;

}
