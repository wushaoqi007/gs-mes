package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.wxcp.domain.helper.CustomOaService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpOaServiceImpl;

/**
 * @author gu_renkai
 * @date 2022/10/27 8:23
 */
@Slf4j
public class CustomOaServiceImpl extends WxCpOaServiceImpl implements CustomOaService {

    private final WxCpService mainService;

    private static final int USER_IDS_LIMIT = 100;

    public CustomOaServiceImpl(WxCpService mainService) {
        super(mainService);
        this.mainService = mainService;
    }

}
