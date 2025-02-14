package com.greenstone.mes.material.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialTaskProgressReportAddReq {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务进度
     */
    private Integer progress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文件url
     */
    private String fileUrl;

    private List<MultipartFile> file;

    private List<String> fileBase64;

}
