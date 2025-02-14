package com.greenstone.mes.bom.domain;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * BOM对象 bom
 *
 * @author gu_renkai
 * @date 2022-01-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@TableName("bom")
public class Bom extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * BOM编码
     */
    @Excel(name = "BOM编码")
    @TableField
    private String code;

    /**
     * BOM名称
     */
    @Excel(name = "BOM名称")
    @TableField
    private String name;

    /**
     * BOM版本
     */
    @Excel(name = "BOM版本")
    @TableField
    private String version;

    /**
     * 项目代码
     */
    @Excel(name = "项目代码")
    @TableField
    private String projectCode;

    /**
     * 物料ID
     */
    @TableField
    private Long materialId;

    /**
     * 发布状态
     */
    @Excel(name = "发布状态")
    @TableField
    private Integer publishStatus;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}