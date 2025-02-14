package com.greenstone.mes.oa.domain.entity;

import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gu_renkai
 * @date 2022/11/24 13:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OaWxUser {

    private WxUserId userId;

    private String name;

}
