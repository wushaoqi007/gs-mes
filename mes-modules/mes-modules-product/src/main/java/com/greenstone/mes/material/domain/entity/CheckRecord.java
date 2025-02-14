package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2022/12/19 10:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckRecord {

    private Long id;
    private String projectCode;
    private String componentCode;
    private String worksheetCode;
    private String materialCode;
    private String materialVersion;
    private String materialName;
    private Integer result;
    private Long number;
    private String ngType;
    private String subNgType;
    private String remark;
    private LocalDateTime time;
    private String sponsor;
    private Boolean hasImage;
    private Integer imageNum;

}
