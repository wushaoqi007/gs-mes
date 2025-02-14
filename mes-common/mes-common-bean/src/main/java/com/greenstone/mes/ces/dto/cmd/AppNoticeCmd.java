package com.greenstone.mes.ces.dto.cmd;

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
public class AppNoticeCmd {

    @NotEmpty(message = "请选择需要通知的单据")
    private List<String> serialNos;

}
