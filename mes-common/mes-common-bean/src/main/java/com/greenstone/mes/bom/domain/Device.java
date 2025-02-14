package com.greenstone.mes.bom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 项目设备/组件表
 *
 * @author wushaoqi
 * @date 2022-05-11-10:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("device")
public class Device extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备编码
     */
    @TableField
    private String code;

    /**
     * 设备名称
     */
    @TableField
    private String name;

    /**
     * 设备版本
     */
    @TableField
    private String version;

    /**
     * bom的ID
     */
    @TableField
    private Long bomId;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 类型
     */
    @TableField
    private Integer type;


}
