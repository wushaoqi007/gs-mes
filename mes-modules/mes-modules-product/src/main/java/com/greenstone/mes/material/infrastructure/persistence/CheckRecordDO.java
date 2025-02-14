package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2022/12/19 10:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_check_record")
public class CheckRecordDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4150657710731431969L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField
    private String projectCode;
    @TableField
    private String componentCode;
    @TableField
    private String worksheetCode;
    @TableField
    private String materialCode;
    @TableField
    private String materialVersion;
    @TableField
    private String materialName;
    @TableField
    private Integer result;
    @TableField
    private Long number;
    @TableField
    private String ngType;
    @TableField
    private String subNgType;
    @TableField
    private String remark;
    @TableField
    private LocalDateTime time;
    @TableField
    private String sponsor;
    @TableField
    private Boolean hasImage;
    @TableField
    private Integer imageNum;
}
