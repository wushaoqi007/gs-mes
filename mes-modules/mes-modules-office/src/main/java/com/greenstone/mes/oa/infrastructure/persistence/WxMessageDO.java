package com.greenstone.mes.oa.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.oa.enums.WxMsgType;
import lombok.*;

import java.io.Serial;

/**
 * 企业微信消息记录
 *
 * @author wushaoqi
 * @date 2023-06-19-13:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("oa_wx_message")
public class WxMessageDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8921262047465032512L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    @TableField
    private String title;
    /**
     * 内容
     */
    @TableField
    private String content;
    /**
     * 跳转链接
     */
    @TableField
    private String url;
    /**
     * 发送的企业
     */
    @TableField
    private String cpId;
    /**
     * 发送的应用
     */
    @TableField
    private Integer agentId;

    /**
     * 消息类型 文本消息: text 文本卡片消息: textcard
     */
    @TableField
    private WxMsgType msgType;
}
