package com.greenstone.mes.table.infrastructure.constant;

public interface TableConst {

    interface DataStatus {
        /**
         * 草稿
         */
        int DRAFT = 1;
        /**
         * 已提交
         */
        int COMMITTED = 2;
        /**
         * 已生效
         */
        int EFFECTIVE = 3;
        /**
         * 已废弃
         */
        int WASTE = 4;
    }

    interface ProcessStatus {
        /**
         * 执行中
         */
        int EXECUTING = 1;
        /**
         * 已完成
         */
        int FINISHED = 2;
        /**
         * 已驳回
         */
        int REJECTED = 3;
        /**
         * 已撤回
         */
        int REVOKED = 4;
    }

    interface Rights {
        String CREATE = "create";
        String UPDATE = "update";
        String DELETE = "delete";
        String VIEW = "view";
        String IMPORT = "import";
        String EXPORT = "export";
        String PRINT = "print";
        String LOCK = "lock";
        String UNLOCK = "unlock";
        String MANAGE = "manage";
    }

}
