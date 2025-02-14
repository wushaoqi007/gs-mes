package com.greenstone.mes.oa.request;

import com.greenstone.mes.oa.enums.WxMsgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxMsgSendCmd {


    /**
     * 接收消息人员（微信userId和系统userId选填一个）
     */
    private List<WxMsgUser> toUser;

    /**
     * 通知标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String content;
    /**
     * 通知跳转链接
     */
    private String url;
    /**
     * 发送通知的企业（可不填，默认自动化企业微信）
     */
    private String cpId;
    /**
     * 发送通知的应用（可不填，默认格林司通系统应用）
     */
    private Integer agentId;

    /**
     * 消息类型 文本消息: text 文本卡片消息: textcard
     */
    private WxMsgType msgType;

    /**
     * 接收消息人员（微信userId和系统userId选填一个）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WxMsgUser {
        /**
         * 微信userId
         */
        private String wxUserId;
        /**
         * 系统userId
         */
        private Long sysUserId;

    }


}
