package com.greenstone.mes.oa.application.helper;

import com.greenstone.mes.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.bean.WxCpUser;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
@Repository
@Service
public class WorkWxHelper {

    public SysUser toSysUser(WxCpUser wxCpUser) {
        SysUser sysUser = SysUser.builder()
                .userName(wxCpUser.getUserId())
                .nickName(wxCpUser.getName())
                .wxUserId(wxCpUser.getUserId())
                .sex(wxCpUser.getGender().getCode())
                .avatar(wxCpUser.getAvatar())
                .position(wxCpUser.getPosition()).build();
        wxCpUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号")).findFirst().ifPresent(a -> sysUser.setEmployeeNo(a.getTextValue()));
        return sysUser;
    }

    public SysUser toSysUser(WxCpUser wxCpUser, SysUser sysUser) {
        SysUser newSysUser = toSysUser(wxCpUser);
        newSysUser.setUserId(sysUser.getUserId());
        return newSysUser;
    }

    public String getEmployeeNo(WxCpUser wxCpUser) {
        return wxCpUser.getExtAttrs().stream().filter(a -> a.getName().equals("工号"))
                .findAny()
                .map(WxCpUser.Attr::getTextValue)
                .orElse(null);
    }

}
