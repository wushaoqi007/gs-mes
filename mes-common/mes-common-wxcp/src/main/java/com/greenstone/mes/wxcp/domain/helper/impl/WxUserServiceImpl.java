package com.greenstone.mes.wxcp.domain.helper.impl;

import com.greenstone.mes.wxcp.domain.helper.WxDeptService;
import com.greenstone.mes.wxcp.domain.helper.WxUserService;
import com.greenstone.mes.wxcp.domain.helper.WxcpService;
import com.greenstone.mes.wxcp.domain.types.CpId;
import com.greenstone.mes.wxcp.domain.types.PhoneNum;
import com.greenstone.mes.wxcp.domain.types.WxDeptId;
import com.greenstone.mes.wxcp.domain.types.WxUserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2022/10/25 8:19
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class WxUserServiceImpl implements WxUserService {

    private final WxcpService wxcpService;

    private final WxDeptService wxWxDeptService;

    @Override
    @Cacheable(value = "wx-user#23#h", key = "#wxUserId.id")
    public WxCpUser getUser(CpId cpId, WxUserId wxUserId) {
        try {
            return wxcpService.getUserService(cpId.id()).getById(wxUserId.id());
        } catch (WxErrorException e) {
            log.error("WxError: Get user failed: ", e);
            throw new RuntimeException("获取企业微信成员信息失败，请稍后再试。");
        }
    }

    @Override
    @Cacheable(value = "wx-user#23#h", key = "#wxUserId.id")
    public WxCpUser getUserInSilent(CpId cpId, WxUserId wxUserId) {
        WxCpUser wxCpUser = null;
        try {
            wxCpUser = wxcpService.getUserService(cpId.id()).getById(wxUserId.id());
        } catch (WxErrorException e) {
            log.error("WxError: Get user failed in silent: ", e);
        }
        return wxCpUser;
    }

    @Override
    public String getUserId(CpId cpId, PhoneNum phoneNum) {
        try {
            return wxcpService.getUserService(cpId.id()).getUserId(phoneNum.num());
        } catch (WxErrorException e) {
            log.error("WxError: Get user failed: ", e);
            throw new RuntimeException("获取企业微信成员信息失败，请稍后再试。");
        }
    }

    @Override
    @CacheEvict(value = "wx-user#23#h", allEntries = true)
    public void refreshUser() {
        log.info("刷新企业微信人员缓存");
    }

    @Override
    public void updateUser(CpId cpId, WxUserId wxUserId, WxCpUser user) {
        try {
            wxcpService.getUserService(cpId.id()).update(user);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取企业微信用户
     *
     * @param cpId       企业ID
     * @param wxDeptId   部门ID
     * @param fetchChild 是否获取子部门用户
     */
    @Override
    public List<WxCpUser> listUser(CpId cpId, WxDeptId wxDeptId, boolean fetchChild) {
        try {
            return wxcpService.getUserService(cpId.id()).listByDepartment(wxDeptId.id(), fetchChild, 0);
        } catch (WxErrorException e) {
            log.error("WxError: Get user list failed: ", e);
            throw new RuntimeException("获取企业微信部门成员信息失败，请稍后再试。");
        }
    }

    /**
     * 获取企业微信所有用户
     *
     * @param cpId 企业ID
     */
    @Override
    public List<WxCpUser> listAllUser(CpId cpId) {
        WxCpDepart rootDept = wxWxDeptService.getRootDept(cpId);
        return listUser(cpId, new WxDeptId(rootDept.getId()), true);
    }

}
