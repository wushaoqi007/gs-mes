package com.greenstone.mes.workflow.infrastructure.consts;

public interface WxApprovalConst {

    interface StatusChangeEvent {
        /**
         * 提单
         */
        int COMMIT = 1;
        /**
         * 同意
         */
        int AGREE = 2;
        /**
         * 驳回
         */
        int REJECT = 3;
        /**
         * 转审
         */
        int CHANGE_ASSIGNOR = 4;
        /**
         * 催办
         */
        int REMIND = 5;
        /**
         * 撤销
         */
        int REVOKE = 6;
        /**
         * 通过后撤销
         */
        int REVOKE_AFTER_FINISH = 8;
        /**
         * 添加备注
         */
        int ADD_REMARK = 10;
        /**
         * 回退给指定审批人
         */
        int REJECT_TO_APPLYER = 11;
        /**
         * 添加审批人
         */
        int ADD_ASSIGNOR = 12;
        /**
         * 加签并同意
         */
        int ADD_ASSIGNOR_AGREE = 13;
        /**
         * 已办理
         */
        int FINISHED = 14;
        /**
         * 已转交
         */
        int CHANGED_APPROVE = 15;
    }

    interface SpStatus {
        /**
         * 审批中
         */
        int WAIT_APPROVE = 1;
        /**
         * 已通过
         */
        int PASSED = 2;
        /**
         * 已驳回
         */
        int REJECTED = 3;
        /**
         * 已撤销
         */
        int REVOKED = 4;
        /**
         * 通过后撤销
         */
        int REVOKED_AFTER_PASSED = 6;
        /**
         * 已删除
         */
        int DELETED = 7;
        /**
         * 已支付
         */
        int PAID = 10;
    }

    interface SpRecord {
        interface SpStatus {
            /**
             * 审批中
             */
            int WAIT_APPROVE = 1;
            /**
             * 已同意
             */
            int AGREED = 2;
            /**
             * 已驳回
             */
            int REJECTED = 3;
            /**
             * 已转审
             */
            int YZS = 4;
        }
    }

}
