package com.greenstone.mes.wxcp.domain.types;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2022/10/24 15:20
 */

public record CpId(@NotEmpty(message = "CpId can not be empty") String id) {

}
