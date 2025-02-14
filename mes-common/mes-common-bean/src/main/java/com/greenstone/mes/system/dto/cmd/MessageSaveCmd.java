package com.greenstone.mes.system.dto.cmd;

import com.greenstone.mes.system.enums.MsgCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSaveCmd {

    @NotEmpty(message = "请指定通知人员")
    private List<Long> recipientIds;

    @NotNull(message = "请填写通知类型")
    private MsgCategory category;

    private String sourceId;

    @NotEmpty(message = "请填写消息标题")
    private String title;

    private String subTitle;

    @NotEmpty(message = "请填写消息内容")
    private String content;

}
