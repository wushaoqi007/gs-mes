package com.greenstone.mes.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/12/19 13:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckRecordSaveCommand {

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
    private LocalDateTime time;
    private String sponsor;
    private List<MultipartFile> files;

}
