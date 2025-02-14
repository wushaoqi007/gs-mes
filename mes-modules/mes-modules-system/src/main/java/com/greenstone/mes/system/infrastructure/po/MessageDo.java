package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import com.greenstone.mes.system.enums.MsgCategory;
import com.greenstone.mes.system.enums.MsgStatus;
import lombok.*;

/**
 * 事件提醒表;
 *
 * @author gu_renkai
 * @date 2023-3-24
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("sys_message")
public class MessageDo extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
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

}