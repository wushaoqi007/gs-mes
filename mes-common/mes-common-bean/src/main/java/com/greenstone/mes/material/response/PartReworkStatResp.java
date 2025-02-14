
package com.greenstone.mes.material.response;

import com.greenstone.mes.common.core.annotation.Excel;
import lombok.*;

/**
 * 返工率
 *
 * @author wushaoqi
 * @date 2022-11-09-13:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PartReworkStatResp {

    @Excel(name = "加工商")
    private String provider;

    @Excel(name = "返工量")
    private Integer reworkTotal;

    @Excel(name = "加工总量")
    private Integer total;

    @Excel(name = "返工率")
    private String reworkRate;

}
