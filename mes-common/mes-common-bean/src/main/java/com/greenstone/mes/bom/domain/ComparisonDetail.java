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
@TableName("comparison_detail")
public class ComparisonDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 比对id
     */
    @TableField
    private Long comparisonId;

    /**
     * 导入详情id
     */
    @TableField
    private Long bomImportDetailId;

    /**
     * 比对结果
     */
    @TableField
    private String result;

    /**
     * 扫描数量
     */
    @TableField
    private Integer scanNumber;


}
