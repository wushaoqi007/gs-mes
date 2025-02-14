package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.PhoneNum;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/25 8:19
 */

public interface WxUserService {

    WxCpUser getUser(CpId cpId, WxUserId wxUserId);

    WxCpUser getUserInSilent(CpId cpId, WxUserId wxUserId);

    String getUserId(CpId cpId, PhoneNum phoneNum);

    List<WxCpUser> listUser(CpId cpId, WxDeptId wxDeptId, boolean fetchChild);

    List<WxCpUser> listAllUser(CpId cpId);

    void refreshUser();

    void updateUser(CpId cpId, WxUserId wxUserId, WxCpUser user);
}
