package com.greenstone.mes.wxcp.domain.helper;

import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;

public interface SpHelper {

    ApplyDataContent buildControl(String id, String control, String type, String mode, String value);

}
