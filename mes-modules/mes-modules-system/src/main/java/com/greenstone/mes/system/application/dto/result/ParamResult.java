package com.greenstone.mes.system.application.dto.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-03-11-15:18
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParamResult {

    private String id;
    private String paramType;
    private String paramName;
    private Boolean multilevel;
    private Integer levels;
    private String status;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private List<ParamData> paramDataList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParamData {
        private String paramValue1;
        private String paramValue2;
        private String paramValue3;
        private Integer orderNum;
        private String remark;
        private List<ParamData> children;
    }

}
