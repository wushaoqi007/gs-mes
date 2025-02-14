package com.greenstone.mes.material.application.helper;

import cn.hutool.core.collection.CollectionUtil;
import com.greenstone.mes.common.core.domain.R;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.common.core.utils.StringUtils;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.file.api.request.FileUploadReq;
import com.greenstone.mes.system.api.domain.FileRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-10-24-13:29
 */
@Slf4j
@Service
public class MaterialFileHelper {

    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 获取文件信息
     *
     * @param id   业务ID
     * @param type 业务ID1:进度报告2:问题报告
     * @return 文件列表
     */
    public List<FileRecord> getFileInfo(Long id, Integer type) {
        R<List<FileRecord>> fileInfo = remoteFileService.info(id, type);
        if (StringUtils.isNull(fileInfo) || StringUtils.isNull(fileInfo.getData())) {
            log.error("文件服务异常，请联系管理员");
            throw new ServiceException("文件服务异常，请联系管理员");
        }
        return fileInfo.getData();
    }

    /**
     * 上传文件
     */
    public void uploadFile(FileUploadReq fileUploadReq) {
        // 文件上传
        if (CollectionUtil.isNotEmpty(fileUploadReq.getFileList()) || CollectionUtil.isNotEmpty(fileUploadReq.getBaseStrList())) {
            R<List<FileRecord>> fileResult = remoteFileService.uploadMultipart(fileUploadReq);
            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
                log.error("文件服务异常，请联系管理员");
                throw new ServiceException("文件服务异常，请联系管理员");
            }
            log.info("文件上传成功：" + fileResult.getData());
        }
    }
}
