package com.greenstone.mes.system.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDeleteCmd {

    @NotEmpty(message = "请选择需要删除的消息")
    private List<String> ids;

}
