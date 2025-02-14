package com.greenstone.mes.workflow.infrastructure.consts;

public interface FlowConst {

    interface Source {
        int WXCP = 1;
    }

    interface NodeType {
        /**
         * 或签
         */
        int OR = 1;

        /**
         * 会签
         */
        int AND = 2;
    }

}
