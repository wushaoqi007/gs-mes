package com.greenstone.mes.material.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatPartsProgressR {

    private String deliverRate;
    private String finishedRate;
    private String projectCode;
    private String componentName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date uploadTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planTime;
    private Integer paperNum;
    private String remark;
}
