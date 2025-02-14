package com.greenstone.mes.market.domain.entity;

import com.greenstone.mes.form.domain.BaseFormDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MarketApplication extends BaseFormDataEntity {

    private String spNo;

    private LocalDateTime expectedArrivalTime;

    private String title;

    private String content;

    private LocalDateTime approveTime;

    private List<Long> approvers;

    private List<Long> copyTo;

    private Long appliedBy;

    private String appliedByName;

    private Long deptId;

    private Integer mailStatus;
    private String mailMsg;

    private List<MarketApplicationAttachment> attachments;

}
