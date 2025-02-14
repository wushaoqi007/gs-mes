package com.greenstone.mes.form.infrastructure.enums;

import lombok.Getter;

@Getter
public enum FieldType {

    input {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    number {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    textarea {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    radio {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    checkbox {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    select {
        @Override
        public String expressValue(String value) {
            return value;
        }
    },
    date {
        @Override
        public String expressValue(String value) {
            return null;
        }
    },
    datetime {
        @Override
        public String expressValue(String value) {
            return null;
        }
    },
    ;

    public String expressValue(String value) {
        return value;
    }

}
