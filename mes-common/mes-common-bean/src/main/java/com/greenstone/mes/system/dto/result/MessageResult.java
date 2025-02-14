package com.greenstone.mes.system.dto.result;


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
public class MessageResult {
    /**
     * id
     */
    private String id;
    /**
     * 状态
     */
    private MsgStatus status;
    /**
     * 分类id
     */
    private MsgCategory category;

    private String title;

    private String subTitle;
    /**
     * 内容
     */
    private String content;

    private LocalDateTime createTime;

}