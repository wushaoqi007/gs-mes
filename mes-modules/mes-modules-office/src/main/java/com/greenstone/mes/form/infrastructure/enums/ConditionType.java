package com.greenstone.mes.form.infrastructure.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum ConditionType {

    equal {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' = '{}'";
            } else {
                express = "{} = '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    notEqual {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' != '{}'";
            } else {
                express = "{} != '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    like {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' like '%{}%'";
            } else {
                express = "{} like '%{}%'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    notLike {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' not like '%{}%'";
            } else {
                express = "{} not like '%{}%'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    empty {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "(data_json -> '$.{}' is null or data_json -> '$.{}' = '')";
            } else {
                express = "({} is null or {} = '')";
            }
            return StrUtil.format(express, field, field);
        }
    },
    notEmpty {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "(data_json -> '$.{}' is not null and data_json -> '$.{}' != '')";
            } else {
                express = "({} is not null and {} != '')";
            }
            return StrUtil.format(express, field, field);
        }
    },
    in {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' in {}";
            } else {
                express = "{} in {}";
            }
            value = value.replace("[", "(").replace("]", ")");
            return StrUtil.format(express, field, value);
        }
    },
    notIn {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' not in {}";
            } else {
                express = "{} not in {}";
            }
            value = value.replace("[", "(").replace("]", ")");
            return StrUtil.format(express, field, value);
        }
    },
    gt {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' > '{}'";
            } else {
                express = "{} > '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    gte {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' >= '{}'";
            } else {
                express = "{} >= '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    lt {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' < '{}'";
            } else {
                express = "{} < '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    lte {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (isExtField) {
                express = "data_json -> '$.{}' <= '{}'";
            } else {
                express = "{} <= '{}'";
            }
            return StrUtil.format(express, field, value);
        }
    },
    between {
        @Override
        public String queryCondition(String field, String type, String value, boolean isExtField) {
            String express;
            if (value.length() < 3) {
                return null;
            }
            String[] condition = value.substring(1, value.length() - 1).split(",");
            if (isExtField) {
                express = "data_json -> '$.{}' between '{}' and '{}'";
            } else {
                express = "{} between '{}' and '{}'";
            }
            return StrUtil.format(express, field, condition[0], condition[1]);
        }
    },
    ;

    public static ConditionType getType(String optionType) {
        return ConditionType.valueOf(ConditionType.class, optionType);
    }

    abstract public String queryCondition(String field, String type, String value, boolean isExtField);
}
