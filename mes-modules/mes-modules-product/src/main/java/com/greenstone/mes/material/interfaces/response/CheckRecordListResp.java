package com.greenstone.mes.material.interfaces.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/19 16:00
 */
@Data
public class CheckRecordListResp {

    private Long id;
    private String projectCode;
    private String componentCode;
    private String worksheetCode;
    private String materialCode;
    private String materialVersion;
    private String materialName;
    private Integer result;
    private Long number;
    private String ngType;
    private String subNgType;
    private String remark;
    private String sponsor;
    private boolean hasImage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    private Integer imageNum;
    private List<Image> images;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Image {
        private String url;
    }

}
