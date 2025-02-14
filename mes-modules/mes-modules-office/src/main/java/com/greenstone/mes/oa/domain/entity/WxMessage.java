package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.oa.enums.WxMsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2023-06-20-11:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxMessage {

    private List<WxMessageUser> toUser;
    private String title;
    private String content;
    private String url;
    private String cpId;
    private Integer agentId;
    private WxMsgType msgType;

}
