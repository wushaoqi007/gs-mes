package com.greenstone.mes.system.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FunctionResult {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Long updateId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
