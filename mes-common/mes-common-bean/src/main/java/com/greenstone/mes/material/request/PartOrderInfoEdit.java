package com.greenstone.mes.material.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author wushaoqi
 * @date 2022-10-12-8:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartOrderInfoEdit {

    @NotEmpty(message = "项目代码不能为空")
    private String projectCode;

    @NotEmpty(message = "机加工单号不能为空")
    private String partOrderCode;

    @NotEmpty(message = "零件号不能为空")
    private String partCode;

    @NotEmpty(message = "零件版本不能为空")
    private String partVersion;

    private String partName;

    /**
     * 类型:加工件、标准件
     */
    private String type;

    /**
     * 是否加急
     */
    private String isFast;

    /**
     * 加工单位
     */
    private String provider;

    /**
     * 加工纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processingTime;

    /**
     * 计划纳期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planTime;

}
