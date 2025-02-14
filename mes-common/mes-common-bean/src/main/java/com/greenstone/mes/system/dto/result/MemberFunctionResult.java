package com.greenstone.mes.system.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wushaoqi
 * @date 2024-10-28-14:11
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberFunctionResult {

    private Long functionId;

    private String functionName;

    private String functionType;

    private String source;

    private String component;

    private String formComponent;

    private Integer orderNum;

    private Long createId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Long updateId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
