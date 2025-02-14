
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
public class MaterialTaskProblemReportAddReq {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 提问人id
     */
    private Long questioner;

    /**
     * 提问人姓名
     */
    private String questionerName;

    /**
     * 问题类型
     */
    private Integer type;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 文件url
     */
    private String fileUrl;

    private List<MultipartFile> file;

    private List<String> fileBase64;
}
