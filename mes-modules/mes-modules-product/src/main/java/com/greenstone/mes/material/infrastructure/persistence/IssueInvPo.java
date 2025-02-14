package com.greenstone.mes.material.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 物料发料清单 material issue inventory
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("material_issue_inventory")
public class IssueInvPo {

    @TableId(type = IdType.AUTO)
    private String id;

    private String projectCode;

    private String deviceName;

    private String componentCode;

    private String componentName;

    private String processCode;

    private String processName;

    private String temporaryCode;

    private String orderNum;

    private String originMaterialName;

    private String materialCode;

    private String materialName;

    private String type;

    private Integer number;

    private Integer suitNumber;

    private Integer totalNumber;

}
