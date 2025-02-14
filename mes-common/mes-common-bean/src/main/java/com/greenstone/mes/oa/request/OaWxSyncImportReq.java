package com.greenstone.mes.oa.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OaWxSyncImportReq {
    /**
     * 企业ID
     */
    private String cpId;

    /**
     * 表格文件
     */
    private MultipartFile file;

}
