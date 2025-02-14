package com.greenstone.mes.oa.infrastructure.persistence;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.greenstone.mes.common.core.web.domain.BaseEntity;
import lombok.*;

/**
 * 宿舍表;
 *
 * @author gu_renkai
 * @date 2023-4-23
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("oa_dorm")
public class DormDo extends BaseEntity {
    /**
     * 宿舍编号
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String dormNo;
    /**
     * 城市
     */
    private String city;
    /**
     * 地址
     */
    private String address;
    /**
     * 房间号
     */
    private String roomNo;
    /**
     * 床位数
     */
    private Integer bedNumber;
    /**
     * 负责人id
     */
    private Long manageBy;
    /**
     * 负责人姓名
     */
    private String manageByName;
    /**
     * 删除标志
     */
    @TableLogic
    private Boolean deleted;

}