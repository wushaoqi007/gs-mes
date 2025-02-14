package com.greenstone.mes.file.application.service;

import com.greenstone.mes.file.infrastructure.persistence.FileRecord;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.file.api.request.FileUploadReq;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件上传接口
 *
 * @author ruoyi
 */
public interface FileService {
    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    SysFile uploadFile(MultipartFile file) throws Exception;

    SysFile uploadFile(MultipartFile file, Integer expireDay) throws Exception;


    /**
     * 文件上传接口（新）
     *
     * @param fileUploadReq 文件关联信息
     * @return 文件上传记录
     * @throws Exception
     */
    List<FileRecord> uploadFileMultipart(FileUploadReq fileUploadReq) throws Exception;

    void clearExpireFile();

    String getFileDomain();

    InputStream getFile(String path);

    FileRecord getFileInfoByPath(String path);
}
