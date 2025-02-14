package com.greenstone.mes.market.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.form.infrastructure.persistence.BaseFormPo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 易耗品采购申请表;
 *
 * @author gu_renkai
 * @date 2023-4-13
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@TableName("market_application")
public class MarketAppDo extends BaseFormPo {

    private String spNo;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 期望到货时间
     */
    private LocalDateTime expectedArrivalTime;

    private LocalDateTime approveTime;
    /**
     * 标题
     */
    private String title;
    /**
     * 申请说明
     */
    private String content;
    /**
     * 抄送人
     */
    private String copyTo;
    /**
     * 审批人
     */
    private String approvers;
    /**
     * 发起人id
     */
    private Long appliedBy;
    /**
     * 发起人姓名
     */
    private String appliedByName;

    /**
     * 发起时间
     */
    private LocalDateTime appliedTime;

    private Long deptId;

    private Integer mailStatus;
    private String mailMsg;

}