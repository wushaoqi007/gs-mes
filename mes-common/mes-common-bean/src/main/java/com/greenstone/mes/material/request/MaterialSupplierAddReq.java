
package com.greenstone.mes.material.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MaterialSupplierAddReq {


    /**
     * 供应商名称
     */
    @NotEmpty(message = "供应商名称不能为空")
    private String name;


    private String phone;

    private String address;

}
