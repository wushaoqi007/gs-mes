package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 设计出图数据来源
 *
 * @author wushaoqi
 * @date 2023-02-22-14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDataDesigner {

    private String partCode;
    private String partName;
    private String partVersion;
    private String componentCode;
    private String componentName;

    private String projectCode;

    private Integer paperNumber;

    private Long partNumber;

    private Date createTime;
    private Date designDeadline;
    private String designer;
    private String fastParts;
    private Boolean updateParts;
    private Boolean repairParts;

}
