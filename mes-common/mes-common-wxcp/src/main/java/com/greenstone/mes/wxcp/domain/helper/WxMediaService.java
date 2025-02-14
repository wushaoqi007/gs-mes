package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.FileId;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;

import java.io.File;
import java.io.InputStream;

/**
 * @author gu_renkai
 * @date 2022/10/25 16:55
 */

public interface WxMediaService {

    File download(CpId cpId, FileId fileId);

    WxMediaUploadResult upload(CpId cpId, Integer appId, String mediaType, InputStream inputStream);

    WxMediaUploadResult uploadByFilePath(Integer agentId, String mediaType, String filePath);

    WxMediaUploadResult uploadByFilePath(String agentName, String mediaType, String filePath);

}