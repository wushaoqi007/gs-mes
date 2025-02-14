package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartsReworkReq {


    /**
     * 生产代码（项目代码）
     */
    @NotEmpty(message = "material.part.rework.lack.project.code")
    private String projectCode;

    @JsonFormat(pattern = "yyyy-MM")
    private Date month;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
