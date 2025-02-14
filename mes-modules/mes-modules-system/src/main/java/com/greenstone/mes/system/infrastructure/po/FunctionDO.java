package com.greenstone.mes.system.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-10-17-16:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName(value = "sys_function")
public class FunctionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String source;

    private String component;

    private String formComponent;

    private Boolean usingProcess;

    private String templateId;

    private Integer orderNum;

    private Long createId;

    private LocalDateTime createTime;

    private Long updateId;

    private LocalDateTime updateTime;
}
