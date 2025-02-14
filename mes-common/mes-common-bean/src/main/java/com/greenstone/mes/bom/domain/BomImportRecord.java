package com.greenstone.mes.bom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.annotation.Excel;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * bom导入记录表
 *
 * @author wushaoqi
 * @date 2022-05-11-9:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("bom_import_record")
public class BomImportRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 导入文件名
     */
    @TableField
    private String fileName;

    /**
     * 项目代码
     */
    @TableField
    private String projectCode;

    /**
     * 零件数量
     */
    @TableField
    private Integer count;

    /**
     * 设计（导入的人）
     */
    @TableField
    private String designer;

}
