package com.greenstone.mes.material.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListQuery {

    private String projectCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectInitiationStart;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectInitiationEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date designDeadlineStart;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date designDeadlineEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customerDeadlineStart;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customerDeadlineEnd;

}
