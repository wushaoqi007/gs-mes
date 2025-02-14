package com.greenstone.mes.external.application.dto.cmd;

import com.greenstone.mes.external.infrastructure.enums.NoticeWay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowNoticeCmd {

    private NoticeWay way;

    private String serialNo;

    private String billTypeName;

    private String title;

    private String subTitle;

    private String content;

}
