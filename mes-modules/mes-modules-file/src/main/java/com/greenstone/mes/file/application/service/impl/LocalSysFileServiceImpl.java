package com.greenstone.mes.file.application.service.impl;

import com.greenstone.mes.file.application.service.FileService;
import com.greenstone.mes.file.infrastructure.mapper.FileRecordMapper;
import com.greenstone.mes.file.infrastructure.persistence.FileRecord;
import com.greenstone.mes.file.infrastructure.utils.FileUploadUtils;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.file.api.request.FileUploadReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件存储
 *
 * @author ruoyi
 */
@Service
public class LocalSysFileServiceImpl implements FileService {

    @Autowired
    private FileRecordMapper fileRecordMapper;

    /**
     * 资源映射路径 前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;

    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFile uploadFile(MultipartFile file) throws Exception {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + localFilePrefix + name;
        return SysFile.builder().url(url).build();
    }

    @Override
    public SysFile uploadFile(MultipartFile file, Integer expireTime) throws Exception {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + localFilePrefix + name;
        return SysFile.builder().url(url).build();
    }

    @Override
    public List<FileRecord> uploadFileMultipart(FileUploadReq fileUploadReq) throws Exception {
        List<FileRecord> fileRecordList = new ArrayList<>();
        for (MultipartFile file : fileUploadReq.getFileList()) {
            String name = FileUploadUtils.upload(localFilePath, file);
            String url = domain + localFilePrefix + name;
            // 保存附件记录
            FileRecord fileRecord = FileRecord.builder().filePath(name).name(file.getOriginalFilename()).build();
            fileRecordMapper.insert(fileRecord);

            fileRecord.setFilePath(url);
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
