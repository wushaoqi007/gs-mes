package com.greenstone.mes.wxcp.domain.helper.impl;

import cn.hutool.core.date.DateUtil;
import com.greenstone.mes.common.core.exception.ServiceException;
import com.greenstone.mes.system.api.RemoteDeptService;
import com.greenstone.mes.system.api.RemoteUserService;
import com.greenstone.mes.system.api.domain.SysDept;
import com.greenstone.mes.system.api.domain.SysUser;
import com.greenstone.mes.wxcp.domain.helper.SpHelper;
import com.greenstone.mes.wxcp.domain.helper.WxMediaService;
import com.greenstone.mes.wxcp.infrastructure.config.WxConfig;
import com.greenstone.mes.wxcp.infrastructure.consts.ApplyConst;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.cp.bean.oa.applydata.ApplyDataContent;
import me.chanjar.weixin.cp.bean.oa.applydata.ContentValue;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Service
public class SpHelperImpl implements SpHelper {

    private final RemoteUserService userService;
    private final RemoteDeptService deptService;
    private final WxMediaService wxMediaService;

    @Override
    public ApplyDataContent buildControl(String id, String control, String type, String mode, String value) {
        ApplyDataContent content = new ApplyDataContent();
        switch (control) {
            case ApplyConst.ControlType.Text, ApplyConst.ControlType.Textarea -> setText(content, value);
            case ApplyConst.ControlType.Number -> setNumber(content, value);
            case ApplyConst.ControlType.Money -> setMoney(content, value);
            case ApplyConst.ControlType.Date -> setDate(content, type, value);
            case ApplyConst.ControlType.Selector -> setSelector(content, type, value);
            case ApplyConst.ControlType.Contact -> setContact(content, mode, value);
            case ApplyConst.ControlType.File -> setFile(content, value);
            default -> throw new ServiceException("不支持的审批组件: " + control + (type == null ? "" : "." + type));
        }
        content.setControl(control);
        content.setId(id);
        return content;
    }

    private void setText(ApplyDataContent content, String value) {
        content.setValue(new ContentValue().setText(value));
    }

    private void setNumber(ApplyDataContent content, String value) {
        content.setValue(new ContentValue().setNewNumber(value));
    }

    private void setMoney(ApplyDataContent content, String value) {
        content.setValue(new ContentValue().setNewMoney(value));
    }

    private void setDate(ApplyDataContent content, String type, String value) {
        ContentValue.Date dateValue = new ContentValue.Date();
        dateValue.setType(type);
        dateValue.setTimestamp(String.valueOf(DateUtil.parse(value, "yyyy-MM-dd").getTime() / 1000));
        content.setValue(new ContentValue().setDate(dateValue));
    }

    /**
     * 选择器值使用逗号分隔(,)
     */
    private void setSelector(ApplyDataContent content, String type, String value) {
        List<ContentValue.Selector.Option> options = Arrays.stream(value.split(",")).map(v -> {
            ContentValue.Selector.Option option = new ContentValue.Selector.Option();
            option.setKey(v);
            return option;
        }).toList();
        ContentValue.Selector selector = new ContentValue.Selector();
        selector.setOptions(options);
        selector.setType(type);
        content.setValue(new ContentValue().setSelector(selector));
    }

    /**
     * 用户或部门值使用逗号分隔(,)，内容为系统内用户id
     */
    private void setContact(ApplyDataContent content, String mode, String value) {
        if (ApplyConst.ValueMode.user.equals(mode)) {
            List<ContentValue.Member> members = Arrays.stream(value.split(","))
                    .map(v -> userService.getUser(SysUser.builder().userId(Long.valueOf(v)).build()))
                    .map(user -> {
                        ContentValue.Member member = new ContentValue.Member();
                        member.setName(user.getNickName());
                        member.setUserId(user.getWxUserId());
                        return member;
                    }).toList();
            content.setValue(new ContentValue().setMembers(members));
        } else if (ApplyConst.ValueMode.department.equals(mode)) {
            List<ContentValue.Department> departments = Arrays.stream(value.split(","))
                    .map(v -> deptService.getSysDept(SysDept.builder().deptId(Long.valueOf(v)).build()))
                    .map(dept -> {
                        ContentValue.Department department = new ContentValue.Department();
                        department.setName(dept.getDeptName());
                        // TODO 这里需要改成 dept openip
                        department.setOpenApiId(String.valueOf(dept.getWxDeptId()));
                        return department;
                    }).toList();
            content.setValue(new ContentValue().setDepartments(departments));
        }
    }

    /**
     * @param value 文件在 file 模块的路径（path）
     */
    private void setFile(ApplyDataContent content, String value) {
        WxMediaUploadResult uploadResult = wxMediaService.uploadByFilePath(WxConfig.SYSTEM, WxConsts.MediaFileType.FILE, value);
        ContentValue.File file = new ContentValue.File();
        file.setFileId(uploadResult.getMediaId());
        content.setValue(new ContentValue().setFiles(List.of(file)));
    }


}
