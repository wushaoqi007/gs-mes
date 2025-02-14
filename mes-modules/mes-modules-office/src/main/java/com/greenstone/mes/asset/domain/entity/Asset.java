package com.greenstone.mes.asset.domain.entity;

import com.greenstone.mes.asset.infrastructure.enums.AssetState;
import com.greenstone.mes.system.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author gu_renkai
 * @date 2023/2/1 9:13
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Asset {

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
    /**
     * 文件号
     */
    private String fileNumber;

    private LocalDate purchasedDate;

    private AssetState state;

    private String location;

    private String unit;

    private Long receivedId;

    private String receivedBy;

    private LocalDateTime receivedTime;

    private String billSn;

    private String remark;

    private User receivedByUser;

}
