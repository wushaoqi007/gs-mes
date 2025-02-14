package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/17 15:38
 */

@Data
@Builder
public class ApprovalContentFile {

    private List<WxMediaId> fileIds;

}
