package com.greenstone.mes.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 附件记录
 *
 * @author wushaoqi
 * @date 2022-08-16-14:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("file_record")
public class FileRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务关联ID
     */
    @TableField
    private Long relationId;

    /**
     * 关联类型（1:进度报告2:问题报告）
     */
    @TableField
    private Integer relationType;

    /**
     * 附件名称
     */
    @TableField
    private String name;

    /**
     * 附件保存路劲
     */
    @TableField
    private String filePath;

    @TableField(exist = false)
    private String url;

    @TableField(exist = false)
    private String localFilePath;
}
