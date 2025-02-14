package com.greenstone.mes.file.api.request;

import com.greenstone.mes.common.core.utils.file.Base64ToMultipartFile;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传请求
 *
 * @author wushaoqi
 * @date 2022-08-16-15:29
 */
@Data
@Builder
public class FileUploadReq {
    /**
     * 业务关联的ID
     */
    private Long relationId;

    /**
     * 业务的类型（1:进度报告2:问题报告
     */
    private Integer relationType;

    /**
     * 文件
     */
    private List<MultipartFile> fileList;

    /**
     * 文件(BASE64字符串)
     */
    private List<String> baseStrList;

    public void setBaseStrList(List<String> baseStrList){
        List<MultipartFile> fileList = new ArrayList<>();
        if(CollectionUtils.isEmpty(this.fileList)){
            for (String base64Str : baseStrList) {
                final String[] base64Array = base64Str.split(",");
                String dataUir, data;
                if (base64Array.length > 1) {
                    dataUir = base64Array[0];
                    data = base64Array[1];
                } else {
                    //默认构建为图片
                    dataUir = "data:image/jpg;base64";
                    data = base64Array[0];
                }
                MultipartFile multipartFile = new Base64ToMultipartFile(data, dataUir);
                fileList.add(multipartFile);
            }
            this.fileList = fileList;
        }
    }
}
