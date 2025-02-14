package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import me.chanjar.weixin.cp.bean.WxCpDepart;

import java.util.List;

public interface WxDeptService {

    WxCpDepart getDept(CpId cpId, WxDeptId wxDeptId);

    List<WxCpDepart> listDept(CpId cpId, WxDeptId wxDeptId);

    WxCpDepart getRootDept(CpId cpId);

    void refreshDept();
}
