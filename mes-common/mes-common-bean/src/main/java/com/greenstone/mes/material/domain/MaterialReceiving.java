package com.greenstone.mes.material.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 领料单
 *
 * @author wushaoqi
 * @date 2022-08-15-8:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("material_receiving")
public class MaterialReceiving extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 单号
     */
    @TableField
    private String code;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 任务状态(0待接收、1备料中、2待领料、3已完成、4已关闭)
     */
    @TableField
    private Integer status;

    /**
     * 接收人
     */
    @TableField
    private String receiveBy;

    /**
     * 备料完成时间
     */
    @TableField
    private Date readyTime;

    /**
     * 截止时间
     */
    @TableField
    private Date deadline;

    /**
     * 删除标识
     */
    @TableLogic(delval = "1",value = "0")
    private Integer deleted;

}
