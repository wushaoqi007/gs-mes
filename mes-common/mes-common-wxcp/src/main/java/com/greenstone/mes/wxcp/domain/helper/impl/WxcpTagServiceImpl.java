package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.wxcp.domain.helper.WxcpTagService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpTagService;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/25 8:19
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WxcpTagServiceImpl implements WxcpTagService {

    private final WxcpServiceImpl wxService;

    @Override
    public List<WxCpTag> tagList(CpId cpId) {
        WxCpTagService wxTagService = wxService.getWxTagService(cpId);
        try {
            return wxTagService.listAll();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WxCpUser> tagUsers(CpId cpId, String tagId) {
        WxCpTagService wxTagService = wxService.getWxTagService(cpId);
        try {
            return wxTagService.listUsersByTagId(tagId);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

}
