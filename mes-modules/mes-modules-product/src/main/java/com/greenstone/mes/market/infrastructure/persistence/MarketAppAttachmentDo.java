package com.greenstone.mes.market.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 市购件采购附件表表;
 *
 * @author gu_renkai
 * @date 2023-4-13
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("market_application_attachment")
public class MarketAppAttachmentDo extends BaseEntity {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 申请单号
     */
    private String serialNo;
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件ID
     */
    private String path;

}