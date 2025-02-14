package com.greenstone.mes.product.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenstone.mes.table.TableEntity;
import com.greenstone.mes.table.infrastructure.annotation.StreamField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPlan extends TableEntity {

    private Long parentId;
    @StreamField("计划状态")
    private String planStatus;
    @StreamField("计划类型")
    private Integer planType;
    @StreamField("项目代码")
    private String projectCode;
    @StreamField("层级")
    private Integer level;
    @StreamField("名称")
    private String name;
    @StreamField("数量")
    private Integer number;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("计划开始时间")
    private Date planStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("计划结束时间")
    private Date planEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("实际开始时间")
    private Date actualStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @StreamField("实际结束时间")
    private Date actualEndTime;
    private Double completionRate;

    private String planPeriod;
    private String actualPeriod;

    private ProductPlanChangeReason changeReason;

}
