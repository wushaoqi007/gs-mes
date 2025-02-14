package com.greenstone.mes.file.api;

import com.greenstone.mes.common.core.constant.ServiceNameConstants;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.file.api.factory.RemoteFileFallbackFactory;
import com.greenstone.mes.file.api.request.FileUploadReq;
import com.greenstone.mes.system.api.domain.FileRecord;
import com.greenstone.mes.system.api.domain.SysFile;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE)
public interface RemoteFileService {
    /**
     * 上传文件
     *
     * @param file 文件信息
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<SysFile> upload(@RequestPart(value = "file") MultipartFile file);

    /**
     * 上传文件
     *
     * @param file      文件
     * @param expireDay 过期天数
     */
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<SysFile> upload(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "expireDay") Integer expireDay);

    /**
     * 根据业务ID及业务类型获取附件信息
     *
     * @param relationId   业务关联ID
     * @param relationType 业务的类型（1:进度报告2:问题报告
     * @return
     */
    @GetMapping("/file/info")
    R<List<FileRecord>> info(@RequestParam("relationId") Long relationId, @RequestParam("relationType") Integer relationType);

    /**
     * 多文件上传(新)
     *
     * @param fileUploadReq 附件关联信息
     * @return 结果
     */
    @PostMapping(value = "/file/upload/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<List<FileRecord>> uploadMultipart(@RequestBody FileUploadReq fileUploadReq);

    @PostMapping("/file/download")
    Response download(@RequestBody Map<String, String> data);


    @GetMapping("/file/info")
    FileRecord getFileInfoByPath(@RequestParam("path") String path);
}
