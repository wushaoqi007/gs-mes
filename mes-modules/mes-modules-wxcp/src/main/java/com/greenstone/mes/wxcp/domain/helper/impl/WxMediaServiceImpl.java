package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.file.api.RemoteFileService;
import com.greenstone.mes.system.api.domain.FileRecord;
import com.greenstone.mes.wxcp.domain.helper.WxMediaService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.FileId;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMediaService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu_renkai
 * @date 2022/10/25 16:54
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class WxMediaServiceImpl implements WxMediaService {

    private final WxcpService wxcpService;

    private final RemoteFileService fileService;

    private final WxConfig wxConfig;

    @Override
    public File download(CpId cpId, FileId fileId) {
        WxCpMediaService mediaService = wxcpService.getMediaService(cpId.id());
        try {
            return mediaService.download(fileId.id());
        } catch (WxErrorException e) {
            log.error("WxError: download file failed", e);
            throw new ServiceException("从企业微信获取文件失败，请稍后再试");
        }
    }

    @Override
    public WxMediaUploadResult upload(CpId cpId, Integer appId, String mediaType, InputStream inputStream) {
        me.chanjar.weixin.cp.api.WxCpService wxCpService = wxcpService.getWxCpService(cpId.id(), appId);
        WxCpMediaService mediaService = wxCpService.getMediaService();
        try {
            return mediaService.upload(mediaType, mediaType, inputStream);
        } catch (WxErrorException e) {
            log.error("WxError: upload file failed", e);
            throw new ServiceException("上传文件失败，请稍后再试");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WxMediaUploadResult uploadByFilePath(Integer agentId, String mediaType, String filePath) {
        Map<String, String> data = new HashMap<>();
        data.put("path", filePath);
        Response response = fileService.download(data);
        FileRecord fileInfo = fileService.getFileInfoByPath(filePath);
        WxMediaUploadResult uploadResult;
        try {
            uploadResult = wxcpService.getWxCpService(agentId).getMediaService().upload(mediaType, response.body().asInputStream(), fileInfo != null ? fileInfo.getName() : filePath.substring(filePath.lastIndexOf("/") + 1));
        } catch (WxErrorException | IOException e) {
            throw new RuntimeException(e);
        }
        return uploadResult;
    }

    @Override
    public WxMediaUploadResult uploadByFilePath(String agentName, String mediaType, String filePath) {
        Integer appId = wxConfig.getAgentId(agentName);
        return uploadByFilePath(appId, mediaType, filePath);
    }

}
