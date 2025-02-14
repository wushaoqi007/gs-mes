package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.WxMediaId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/11/22 14:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalComment {

    private WxUserId wxUserId;

    private String content;

    private Long commentTime;

    private List<WxMediaId> mediaIds;

}
