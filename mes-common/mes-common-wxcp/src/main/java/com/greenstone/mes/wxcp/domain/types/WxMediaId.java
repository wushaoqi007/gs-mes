package com.greenstone.mes.wxcp.domain.types;

import javax.validation.ValidationException;

/**
 * @author gu_renkai
 * @date 2022/10/26 8:13
 */

public record WxMediaId(String id) {

    public WxMediaId {
        if (id == null) {
            throw new ValidationException("WxFileId: id is necessary");
        }
    }
}
