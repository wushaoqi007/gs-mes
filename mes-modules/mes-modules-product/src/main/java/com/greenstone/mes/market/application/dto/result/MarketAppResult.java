package com.greenstone.mes.market.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.external.enums.ProcessStatus;
import com.greenstone.mes.market.domain.entity.MarketApplicationAttachment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MarketAppResult {

    private String id;

    private String serialNo;

    private String spNo;

    private ProcessStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expectedArrivalTime;

    private String title;

    private String content;

    private List<Long> approvers;

    private List<Long> copyTo;

    private Long appliedBy;

    private String appliedByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedTime;

    private Integer mailStatus;
    private String mailMsg;

    private List<MarketApplicationAttachment> attachments;

}
