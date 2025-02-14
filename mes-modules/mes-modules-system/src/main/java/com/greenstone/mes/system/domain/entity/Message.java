package com.greenstone.mes.system.domain.entity;

import com.greenstone.mes.system.enums.MsgCategory;
import com.greenstone.mes.system.enums.MsgStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    /**
     * id
     */
    private String id;
    /**
     * 接收人id
     */
    private Long recipientId;
    /**
     * 状态
     */
    private MsgStatus status;
    /**
     * 分类id
     */
    private MsgCategory category;
    /**
     * 来源id
     */
    private String sourceId;

    private String title;

    private String subTitle;
    /**
     * 内容
     */
    private String content;

    private LocalDateTime createTime;

}