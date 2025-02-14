package com.greenstone.mes.external.infrastructure.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审批类型
 *
 * @author gu_renkai
 * @date 2023/2/24 15:06
 */

@Getter
@AllArgsConstructor
public enum ApproveType {
    ASSIGN(0, "assign", "指定成员"),
    SELF_SELECT(1, "optional", "申请人自选"),
    ;
    @JSONField
    @EnumValue
    private final int id;

    private final String code;

    private final String name;

}
