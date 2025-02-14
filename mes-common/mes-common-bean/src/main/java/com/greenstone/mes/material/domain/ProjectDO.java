package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 项目表
 *
 * @author wushaoqi
 * @date 2023-01-09-9:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_project")
public class ProjectDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    @TableField
    private String customerName;
    @TableField
    private String customerShortName;

    /**
     * 立项日期
     */
    @TableField
    private Date projectInitiationTime;

    /**
     * GS组织
     */
    @TableField
    private String gsOrganization;

    /**
     * 生产类型
     */
    @TableField
    private String productionType;

    /**
     * 项目（产品）名称
     */
    @TableField
    private String projectName;

    /**
     * 数量
     */
    @TableField
    private Integer number;

    /**
     * 单位
     */
    @TableField
    private String unit;

    /**
     * 设计纳期
     */
    @TableField
    private Date designDeadline;

    /**
     * 客户纳期
     */
    @TableField
    private Date customerDeadline;

    /**
     * 订单号
     */
    @TableField
    private String orderCode;

    /**
     * 订单接收日
     */
    @TableField
    private Date orderReceiveTime;

    /**
     * 客户担当
     */
    @TableField
    private String customerDirector;

    /**
     * 设计担当
     */
    @TableField
    private String designerDirector;

    /**
     * 电气担当
     */
    @TableField
    private String electricalDirector;

    /**
     * 是否需要软件部参与
     */
    @TableField("is_software_join")
    private Boolean softwareJoin;

    /**
     * 软件担当
     */
    @TableField
    private String softwareDirector;

    /**
     * 业务担当
     */
    @TableField
    private String businessDirector;

    /**
     * 备注
     */
    @TableField
    private String remark;

    /**
     * 一次报价
     */
    @TableField
    private String firstQuotation;

    /**
     * 最终报价单
     */
    @TableField
    private String lastQuotation;

    /**
     * 同一订单
     */
    @TableField
    private String sameOrder;

}
