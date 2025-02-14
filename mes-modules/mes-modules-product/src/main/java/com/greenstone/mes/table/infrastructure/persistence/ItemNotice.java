package com.greenstone.mes.table.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.LongListTypeHandler;
import com.greenstone.mes.table.infrastructure.config.mubatisplus.StringListTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "item_notice", autoResultMap = true)
public class ItemNotice {

    @TableId(type = IdType.AUTO)
    private Long noticeId;

    private Long functionId;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> itemActions;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> predefineEmails;

    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> emailSendUsers;

    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> emailCopyUsers;

    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> wxMsgUsers;


}
