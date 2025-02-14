package com.greenstone.mes.oa.application.service;

import com.greenstone.mes.common.mybatisplus.IServiceWrapper;
import com.greenstone.mes.oa.domain.OaWxFile;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;

import java.util.List;

/**
 * 企业微信媒体文件Service
 *
 * @author wushaoqi
 * @date 2022-08-22-10:03
 */
public interface OaWxFileService extends IServiceWrapper<OaWxFile> {


    /**
     * @param cpId     cpId
     * @param spNo     spNo
     * @param mediaIds mediaIds
     */
    void saveOrUpdateFile(CpId cpId, SpNo spNo, List<WxMediaId> mediaIds);
}
