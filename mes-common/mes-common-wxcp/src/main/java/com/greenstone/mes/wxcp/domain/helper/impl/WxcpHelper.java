package com.greenstone.mes.wxcp.domain.helper.impl;

import me.chanjar.weixin.cp.bean.WxCpUser;

public class WxcpHelper {

    public static String getEmployeeNo(WxCpUser wxCpUser){
        return wxCpUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findAny().map(WxCpUser.Attr::getTextValue).orElse(null);
    }

}
