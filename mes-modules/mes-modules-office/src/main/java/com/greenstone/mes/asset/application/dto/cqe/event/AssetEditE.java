package com.greenstone.mes.asset.application.dto.cqe.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/8 14:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetEditE {

    private String barCode;

    private Long editedBy;

    private String editedByName;

    private LocalDateTime editedTime;

    private String changeContent;

}
