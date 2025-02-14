package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 采购单
 *
 * @author wushaoqi
 * @date 2022-05-16-13:00
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("purchase_order")
public class ProcessOrderDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 采购单号
     */
    @TableField
    private String code;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 状态
     */
    @TableField
    private String status;

    /**
     * 已到数
     */
    @TableField
    private Integer getNumber;

    /**
     * 采购数
     */
    @TableField
    private Long purchaseNumber;

    /**
     * 包含返工件:是、否
     */
    @TableField
    private String isRework;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 确认时间
     */
    @TableField
    private Date confirmTime;

    /**
     * 确认人
     */
    @TableField
    private String confirmBy;

    /**
     * 是否变更中
     */
    @TableField
    private String isChanging;

    /**
     * 公司类型（1:无锡格林司通自动化设备有限公司、2:无锡格林司通科技有限公司）
     */
    @TableField
    private Integer companyType;

}
