
package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialSupplierEditReq {

    @NotNull(message = "ID不为空")
    private Long id;

    /**
     * 供应商名称
     */
    private String name;

    private String phone;

    private String address;

}
