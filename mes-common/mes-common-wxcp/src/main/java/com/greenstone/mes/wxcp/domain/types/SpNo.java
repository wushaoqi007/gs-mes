package com.greenstone.mes.wxcp.domain.types;

import javax.validation.constraints.NotEmpty;

/**
 * @author gu_renkai
 * @date 2022/10/26 8:13
 */

public record SpNo(@NotEmpty(message = "SpNo can not be empty") String no) {
}
