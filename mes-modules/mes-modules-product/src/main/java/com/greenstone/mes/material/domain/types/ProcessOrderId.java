package com.greenstone.mes.material.domain.types;

import javax.validation.ValidationException;

/**
 * @author gu_renkai
 * @date 2022/10/31 9:13
 */
public record ProcessOrderId(Long id) {

    public ProcessOrderId {
        if (id == null) {
            throw new ValidationException("id不能为空");
        }
    }

}
