package com.greenstone.mes.asset.application.dto.result;

import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/1 9:13
 */
@Data
public class AssetR {

    private Long id;

    private Long parentId;

    private Long typeId;

    private String typeCode;

    private String typeName;

    private String typeHierarchy;

    private String barCode;

    private String name;

    private String sn;

    private String specification;

    private LocalDate purchasedDate;

    private AssetState state;

    private String location;

    private String unit;

    private Long receivedId;

    private String receivedBy;

    private LocalDateTime receivedTime;

    private String billSn;

    private String remark;

}
