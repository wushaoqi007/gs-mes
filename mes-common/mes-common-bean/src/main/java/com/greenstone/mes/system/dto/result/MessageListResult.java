package com.greenstone.mes.system.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MessageListResult {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}