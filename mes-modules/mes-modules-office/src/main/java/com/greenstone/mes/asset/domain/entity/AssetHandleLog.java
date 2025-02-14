package com.greenstone.mes.asset.domain.entity;

import com.greenstone.mes.asset.infrastructure.enums.AssetHandleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:05
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AssetHandleLog {

    private Long id;

    private String barCode;

    private LocalDateTime handleTime;

    private AssetHandleType handleType;

    private Long billId;

    private Long handlerId;

    private String handlerName;

    private String content;

}
