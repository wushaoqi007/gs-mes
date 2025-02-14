package com.greenstone.mes.oa.domain.entity;

import lombok.Builder;
import lombok.Data;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentValue;

import java.util.List;


@Data
@Builder
public class ApprovalMembers {

    private List<ContentValue.Member> members;

}
