package com.greenstone.mes.bom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * bom导入记录详情表
 *
 * @author wushaoqi
 * @date 2022-05-11-10:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("bom_import_detail")
public class BomImportDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 导入记录的ID
     */
    @TableField
    private Long recordId;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;


    /**
     * 机加工单号
     */
    @TableField
    private String partOrderCode;

    /**
     * 组件号
     */
    @TableField
    private String componentCode;

    /**
     * 设备组件的名称
     */
    @TableField
    private String componentName;

    /**
     * 零件号
     */
    @TableField
    private String code;

    /**
     * 版本
     */
    @TableField
    private String version;

    /**
     * 零件名称
     */
    @TableField
    private String name;

    /**
     * 购买区分
     */
    @TableField
    private String buyLimit;


    /**
     * 数量
     */
    @TableField
    private Long materialNumber;

    /**
     * 图纸数量
     */
    @TableField
    private Integer paperNumber;

    /**
     * 表面处理
     */
    @TableField
    private String surfaceTreatment;

    /**
     * 原材料
     */
    @TableField
    private String rawMaterial;

    /**
     * 质量g
     */
    @TableField
    private String weight;

    /**
     * 设计者
     */
    @TableField
    private String designer;

    /**
     * 打印日期
     */
    @TableField
    private Date printTime;

    /**
     * 说明备注
     */
    @TableField
    private String remark;


}
