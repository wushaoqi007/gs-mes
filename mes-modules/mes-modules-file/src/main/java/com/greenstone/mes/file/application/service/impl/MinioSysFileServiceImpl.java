package com.greenstone.mes.file.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.greenstone.mes.common.core.utils.file.FileUtils;
import com.greenstone.mes.file.application.service.FileService;
import com.greenstone.mes.file.infrastructure.config.MinioConfig;
import com.greenstone.mes.file.infrastructure.mapper.FileRecordMapper;
import com.greenstone.mes.file.infrastructure.persistence.FileRecord;
import com.greenstone.mes.file.infrastructure.utils.FileUploadUtils;
import com.greenstone.mes.system.api.domain.SysFile;
import com.greenstone.mes.file.api.request.FileUploadReq;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Minio 文件存储
 *
 * @author ruoyi
 */
@Primary
@Slf4j
@Service
public class MinioSysFileServiceImpl implements FileService {

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClient client;

    @Autowired
    private FileRecordMapper fileRecordMapper;


    /**
     * 本地文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public SysFile uploadFile(MultipartFile file) throws Exception {
        return uploadFile(file, null);
    }

    @Override
    public SysFile uploadFile(MultipartFile file, Integer expireDay) throws Exception {
        FileRecord fileRecord = upload(file);
        if (expireDay != null) {
            fileRecord.setExpireTime(System.currentTimeMillis() + expireDay * 24 * 60 * 60 * 1000);
        }
        String url = minioConfig.getUrl() + fileRecord.getFilePath();
        fileRecordMapper.insert(fileRecord);
        return SysFile.builder().id(fileRecord.getId())
                .name(FileUtils.getName(fileRecord.getName()))
                .path(fileRecord.getFilePath())
                .url(url)
                .originalName(file.getOriginalFilename()).build();
    }

    @Override
    public List<FileRecord> uploadFileMultipart(FileUploadReq fileUploadReq) {
        throw new RuntimeException("不支持的操作");
    }

    @Override
    public void clearExpireFile() {
        LambdaQueryWrapper<FileRecord> queryWrapper = Wrappers.lambdaQuery(FileRecord.class).lt(FileRecord::getExpireTime, System.currentTimeMillis());
        List<FileRecord> fileRecords = fileRecordMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(fileRecords)) {
            log.info("No file needs clear.");
            return;
        }
        Iterable<Result<DeleteError>> results = delete(fileRecords);
        for (Result<DeleteError> result : results) {
            try {
                System.out.println(result.get());
            } catch (Exception e) {
                log.error("An error occur when clear expire files", e);
                throw new RuntimeException(e);
            }
        }
        List<Long> fileIds = fileRecords.stream().map(FileRecord::getId).toList();
        fileRecordMapper.deleteBatchIds(fileIds);
    }

    @Override
    public String getFileDomain() {
        return minioConfig.getUrl();
    }

    @Override
    public InputStream getFile(String path) {
        try {
            String bucket = path.substring(1, path.indexOf("/", 2));
            String object = path.substring(path.indexOf("/", 2));
            return client.getObject(GetObjectArgs.builder().bucket(bucket).object(object).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileRecord getFileInfoByPath(String path) {
        return fileRecordMapper.selectOne(Wrappers.query(FileRecord.builder().filePath(path).build()));
    }

    private FileRecord upload(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = FileUploadUtils.extractFilename(file);
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build();
        client.putObject(args);
        String originalName = file.getOriginalFilename();
        String path = "/" + minioConfig.getBucketName() + "/" + fileName;
        return FileRecord.builder().name(originalName).filePath(path).saveMode("minio").build();
    }

    private Iterable<Result<DeleteError>> delete(List<FileRecord> fileRecords) {
        List<DeleteObject> deleteObjects = fileRecords.stream().map(f -> f.getFilePath().substring(f.getFilePath().indexOf("/", 2)))
                .map(DeleteObject::new).toList();
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(minioConfig.getBucketName()).objects(deleteObjects).build();
        return client.removeObjects(removeObjectsArgs);
    }

}
