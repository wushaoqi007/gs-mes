package com.greenstone.mes.external.domain.entity.node;

import com.greenstone.mes.external.infrastructure.enums.*;
import lombok.Data;

import java.util.List;

/**
 * @author gu_renkai
 * @date 2023/2/24 15:32
 */
@Data
public class NodeParams {

    /**
     * 审批类型 审批节点有此属性
     */
    private ApproveType approveType;
    /**
     * 选人方式 审批类型为指定成员或申请人自选时有效
     */
    private SelectionMode selectionMode;
    /**
     * 多人审批方式 选人方式为多选时有效
     */
    private MultiApproveMode multiApproveMode;
    /**
     * 受让人（办理人） 审批和抄送节点有此属性
     */
    List<Assignee> assignees;

    /**
     * 条件组 条件节点有此属性
     */
    private List<ConditionGroup> conditionGroups;

    /**
     * 通知 审批和抄送节点有此属性
     */
    private List<Notification> notifications;


    @Data
    public static class Notification {
        private NotificationPoint point;

        private NoticeWay way;

        private NotificationTarget target;

        private List<Assignee> targetUsers;
    }
}
