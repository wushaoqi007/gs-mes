package com.greenstone.mes.bom.request;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 通过二维码添加BOM的请求类
 */

@Data
public class BomQrCodeAddReq {

    @NotEmpty(message = "缺少机加工单号")
    private String partOrderCode;

    @NotEmpty(message = "缺少项目代码")
    private String projectCode;

    @NotEmpty(message = "缺少组件名称")
    @Length(min = 3, message = "组件名称包含组件号，长度至少为3")
    private String componentCodeName;

    @NotEmpty(message = "缺少零件名称")
    private String partCodeName;

    @NotEmpty(message = "缺少零版本")
    private String partVersion;

    @NotNull(message = "缺少零件数量")
    private Long partNumber;

    @NotNull(message = "缺少图纸张数")
    private Integer paperNumber;

    @NotEmpty(message = "缺少重量")
    private String weight;

    @NotEmpty(message = "缺少原材料")
    private String rawMaterial;

    private String surfaceTreatment;

    @NotEmpty(message = "缺少设计姓名")
    private String designer;

    @Max(value = 6, message = "购买原因填写1-6内数字，1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他")
    @Min(value = 1, message = "购买原因填写1-6内数字，1：正常新增、2：设计失误、3：需求变更、4：仓库丢失、5：装配丢失，6：其他")
    @NotNull(message = "购买原因不能为空")
    private Integer purchaseReason;

    @NotNull(message = "公司类型不能为空")
    private Integer companyType;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
