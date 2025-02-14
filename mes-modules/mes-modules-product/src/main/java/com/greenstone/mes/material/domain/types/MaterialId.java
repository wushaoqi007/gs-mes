package com.greenstone.mes.material.domain.types;

import javax.validation.ValidationException;

/**
 * @author gu_renkai
 * @date 2022/10/31 9:53
 */

public record MaterialId(Long id) {

    public MaterialId {
        if (id == null) {
            throw new ValidationException("id不能为空");
        }
    }

}
