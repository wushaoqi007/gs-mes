package com.greenstone.mes.product.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.table.TablePo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author wushaoqi
 * @date 2024-10-30-14:54
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName(value = "product_plan")
public class ProductPlanDO extends TablePo {

    private Long parentId;
    private String planStatus;
    private Integer planType;
    private String projectCode;
    private Integer level;
    private String name;
    private Integer number;
    private Date planStartTime;
    private Date planEndTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private Double completionRate;
}
