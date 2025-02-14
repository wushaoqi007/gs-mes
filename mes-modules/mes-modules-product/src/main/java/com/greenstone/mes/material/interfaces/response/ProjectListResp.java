package com.greenstone.mes.material.interfaces.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class ProjectListResp {

    private Long id;

    private String projectCode;

    private String customerName;
    private String customerShortName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectInitiationTime;

    private String gsOrganization;

    private String productionType;

    private String projectName;

    private Integer number;

    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date designDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date customerDeadline;

    private String orderCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date orderReceiveTime;

    private String customerDirector;

    private String designerDirector;

    private String electricalDirector;

    private Boolean softwareJoin;

    private String softwareDirector;

    private String businessDirector;

    private String remark;

    private String firstQuotation;

    private String lastQuotation;

    private String sameOrder;

}
