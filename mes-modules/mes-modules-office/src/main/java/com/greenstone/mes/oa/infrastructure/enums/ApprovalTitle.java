package com.greenstone.mes.oa.infrastructure.enums;

import lombok.Data;

/**
 * @author gu_renkai
 * @date 2022/11/17 15:56
 */

public enum ApprovalTitle {
    ,
    ;

    @Data
    public static class VacationTitle {
        /**
         * 假勤标题
         */
        private String vacation;
        /**
         * 备注标题
         */
        private String comment;
        /**
         * 附件标题
         */
        private String attachment;

        public VacationTitle(String vacation, String comment, String attachment) {
            this.vacation = vacation;
            this.comment = comment;
            this.attachment = attachment;
        }
    }


}
