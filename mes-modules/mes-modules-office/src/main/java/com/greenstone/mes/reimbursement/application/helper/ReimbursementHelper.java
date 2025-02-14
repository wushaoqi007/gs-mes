package com.greenstone.mes.reimbursement.application.helper;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.greenstone.mes.external.enums.ProcessStatus;
import me.chanjar.weixin.cp.bean.templatecard.HorizontalContent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushaoqi
 * @date 2024-01-11-10:47
 */
@Service
public class ReimbursementHelper {

    public String getTitle(ProcessStatus status, String approveBy) {
        String title = "";
        if (status == ProcessStatus.APPROVED) {
            title = "你的报销申请已通过";
        }
        if (status == ProcessStatus.REJECTED) {
            title = StrUtil.format("{}驳回了你的报销申请", approveBy);
        }
        return title;
    }

    public String getDescription(String typeName, String reason, Double total) {
        return StrUtil.format("<div>报销类型：{}</div><div>报销事由：{}</div><div>总计费用：{}</div>",
                typeName, reason, total);
    }

    public String getDetailUrl() {
        return "https://www.baidu.com/";
    }

    public String getBtnTxt() {
        return "查看详情";
    }

    public String getMainTitleTitle(String approvedBy) {
        return StrUtil.format("{}的报销申请", approvedBy);
    }

    public String genTaskId(String serialNo) {
        return "reimbursement_approve@" + serialNo + "@" + IdUtil.fastUUID();
    }

    public String getSerialNo(String taskId) {
        String[] strs = taskId.split("@");
        return strs[1];
    }

    public List<HorizontalContent> getContents(String typeName, String reason, Double total) {
        List<HorizontalContent> contents = new ArrayList<>();
        contents.add(HorizontalContent.builder().keyname("报销类型：").value(typeName).build());
        contents.add(HorizontalContent.builder().keyname("报销事由：").value(reason).build());
        contents.add(HorizontalContent.builder().keyname("总计费用：").value(String.valueOf(total)).build());
        return contents;
    }
}
