package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpDepartmentService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/24 16:49
 */

@RequiredArgsConstructor
@Slf4j
@Service
public class WxDeptServiceImpl implements WxDeptService {

    private final WxcpService externalWxcpService;


    /**
     * 获取企业微信部门
     *
     * @param cpId     企业ID
     * @param wxDeptId 部门ID
     */
    @Override
    @Cacheable(value = "wx-dept#23#h", key = "#wxDeptId.id")
    public WxCpDepart getDept(CpId cpId, WxDeptId wxDeptId) {
        WxCpDepartmentService departmentService = externalWxcpService.getDepartmentService(cpId.id());
        try {
            return departmentService.get(wxDeptId.id());
        } catch (WxErrorException e) {
            log.error("WxError: get dept failed", e);
            throw new ServiceException("获取企业微信部门信息失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "wx-dept#23#h", allEntries = true)
    public void refreshDept() {
        log.info("刷新企业微信部门缓存");
    }

    /**
     * 获取企业微信部门列表
     *
     * @param cpId     企业ID
     * @param wxDeptId 部门ID，可为空，为空时获取所有部门
     */
    @Override
    public List<WxCpDepart> listDept(CpId cpId, WxDeptId wxDeptId) {
        WxCpDepartmentService departmentService = externalWxcpService.getDepartmentService(cpId.id());
        try {
            return wxDeptId == null ? departmentService.list(null) : departmentService.list(wxDeptId.id());
        } catch (WxErrorException e) {
            log.error("Error get wx dept", e);
            throw new ServiceException("获取企业微信部门信息失败: " + e.getMessage());
        }
    }


    /**
     * 获取企业微信根部门
     *
     * @param cpId 企业ID
     */
    @Override
    public WxCpDepart getRootDept(CpId cpId) {
        List<WxCpDepart> wxCpDeparts = listDept(cpId, null);
        WxCpDepart rootDepart = wxCpDeparts.stream().filter(d -> d.getParentId() == 0).findFirst().orElse(null);
        if (rootDepart == null) {
            log.error("WxError: can not find root department");
            throw new RuntimeException("无法获取企业微信根部门信息，请稍后再试。");
        }
        return rootDepart;
    }

}
