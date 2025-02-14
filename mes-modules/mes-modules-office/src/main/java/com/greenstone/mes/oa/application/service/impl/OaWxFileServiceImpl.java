package com.greenstone.mes.oa.application.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.greenstone.mes.oa.application.service.OaWxFileService;
import com.greenstone.mes.oa.domain.OaWxFile;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.SpNo;
import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.oa.infrastructure.mapper.OaWxFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wushaoqi
 * @date 2022-08-22-10:05
 */
@Slf4j
@Service
public class OaWxFileServiceImpl extends ServiceImpl<OaWxFileMapper, OaWxFile> implements OaWxFileService {

    @Override
    public void saveOrUpdateFile(CpId cpId, SpNo spNo, List<WxMediaId> mediaIds) {

        if (CollectionUtil.isNotEmpty(mediaIds)) {
            QueryWrapper<OaWxFile> queryWrapper = Wrappers.query(OaWxFile.builder().spNo(spNo.no()).cpId(cpId.id()).build());
            List<OaWxFile> fileList = list(queryWrapper);
            List<String> existMedias = fileList.stream().map(OaWxFile::getMediaId).toList();
            mediaIds = mediaIds.stream().filter(id -> !existMedias.contains(id.id())).toList();
            for (WxMediaId mediaId : mediaIds) {
                OaWxFile oaWxFile = OaWxFile.builder().spNo(spNo.no()).cpId(cpId.id()).mediaId(mediaId.id()).build();
                save(oaWxFile);
            }
        }
    }
}
