package com.greenstone.mes.wxcp.domain.types;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2022/10/24 15:20
 */

public record PhoneNum(@NotEmpty(message = "PhoneNum can not be empty") String num) {

}
