package com.greenstone.mes.material.domain.types;

import javax.validation.ValidationException;

/**
 * @author gu_renkai
 * @date 2022/10/31 9:21
 */

public record ProcessPartId(Long id) {

    public ProcessPartId {
        if (id == null) {
            throw new ValidationException("id不能为空");
        }
    }

}