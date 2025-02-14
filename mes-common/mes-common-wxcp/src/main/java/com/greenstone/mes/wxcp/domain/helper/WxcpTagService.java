package com.greenstone.mes.wxcp.domain.helper;

import com.greenstone.mes.wxcp.domain.types.CpId;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/25 8:19
 */

public interface WxcpTagService {

    List<WxCpTag> tagList(CpId cpId);

    List<WxCpUser> tagUsers(CpId cpId, String tagId);
}
