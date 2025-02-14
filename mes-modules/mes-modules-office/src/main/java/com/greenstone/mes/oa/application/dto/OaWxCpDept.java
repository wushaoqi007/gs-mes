package com.greenstone.mes.oa.application.dto;

import com.greenstone.mes.common.core.annotation.TreeChildren;
import com.greenstone.mes.common.core.annotation.TreeId;
import com.greenstone.mes.common.core.annotation.TreeParentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.WxCpDepart;

import java.io.Serial;
import java.util.List;

/**
 * OA企业微信通讯录部门继承类
 *
 * @author wushaoqi
 * @date 2022-06-10-15:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OaWxCpDept extends WxCpDepart {

    @Serial
    private static final long serialVersionUID = 6317413579521528L;
    @TreeId
    private Long id;

    @TreeParentId
    private Long parentId;

    @TreeChildren
    private List<OaWxCpDept> childList;


}
