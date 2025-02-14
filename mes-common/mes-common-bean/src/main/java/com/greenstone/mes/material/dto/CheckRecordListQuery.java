package com.greenstone.mes.material.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author gu_renkai
 * @date 2022/12/19 13:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckRecordListQuery {

    private String projectCode;
    private String sponsor;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private Integer result;
    private String materialCode;
    private String materialVersion;
    private String materialName;
    private String ngType;
    private String subNgType;

}
