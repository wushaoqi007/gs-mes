package com.greenstone.mes.material.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wushaoqi
 * @date 2023-01-09-11:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private Long id;

    @NotEmpty(message = "产品代码不能为空")
    private String projectCode;

    @NotEmpty(message = "客户名称不能为空")
    private String customerName;
    @NotEmpty(message = "客户简称不能为空")
    private String customerShortName;

    private Date projectInitiationTime;

    private String gsOrganization;

    private String productionType;

    private String projectName;

    @NotNull(message = "数量不能为空")
    private Integer number;

    private String unit;

    @NotNull(message = "设计纳期不能为空")
    private Date designDeadline;

    @NotNull(message = "客户纳期不能为空")
    private Date customerDeadline;

    private String orderCode;

    private Date orderReceiveTime;

    @NotEmpty(message = "客户担当不能为空")
    private String customerDirector;

    private String designerDirector;

    private String electricalDirector;

    private Boolean softwareJoin;

    private String softwareDirector;

    @NotEmpty(message = "业务担当不能为空")
    private String businessDirector;

    private String remark;

    private String firstQuotation;

    private String lastQuotation;

    private String sameOrder;

}
