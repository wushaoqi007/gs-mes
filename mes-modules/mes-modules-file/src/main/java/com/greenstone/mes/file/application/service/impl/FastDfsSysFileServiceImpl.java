package com.greenstone.mes.file.application.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.greenstone.mes.file.application.service.FileService;
import com.greenstone.mes.file.infrastructure.mapper.FileRecordMapper;
import com.greenstone.mes.file.infrastructure.persistence.FileRecord;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.file.api.request.FileUploadReq;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FastDFS 文件存储
 *
 * @author ruoyi
 */

public class FastDfsSysFileServiceImpl implements FileService {
    /**
     * 域名或本机访问地址
     */
    @Value("${fdfs.domain}")
    public String domain;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private FileRecordMapper fileRecordMapper;

    /**
     * FastDfs文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFile uploadFile(MultipartFile file) throws Exception {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return SysFile.builder().url(domain + "/" + storePath.getFullPath()).build();
    }

    @Override
    public SysFile uploadFile(MultipartFile file, Integer expireTime) throws Exception {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return SysFile.builder().url(domain + "/" + storePath.getFullPath()).build();
    }

    @Override
    public List<FileRecord> uploadFileMultipart(FileUploadReq fileUploadReq) throws Exception {
        List<FileRecord> fileRecordList = new ArrayList<>();
        for (MultipartFile file : fileUploadReq.getFileList()) {
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                    FilenameUtils.getExtension(file.getOriginalFilename()), null);
            // 保存附件记录
            FileRecord fileRecord = FileRecord.builder().filePath(storePath.getPath()).name(file.getName()).build();
            fileRecordMapper.insert(fileRecord);

            fileRecord.setFilePath(storePath.getFullPath());
            fileRecordList.add(fileRecord);
        }

        return fileRecordList;
    }

    @Override
    public void clearExpireFile() {

    }

    @Override
    public String getFileDomain() {
        return domain;
    }

    @Override
    public InputStream getFile(String path) {
        return null;
    }

    @Override
    public FileRecord getFileInfoByPath(String path) {
        return null;
    }
}
