package com.greenstone.mes.file.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@TableName("file_record")
public class FileRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件保存路劲
     */
    private String filePath;

    /**
     * 保存方式 minio、local
     */
    private String saveMode;

    /**
     * 过期时间（毫秒值）
     */
    private Long expireTime;

    /**
     * 是否已经清理
     */
    private Boolean cleared;
}
