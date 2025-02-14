package com.greenstone.mes.common.core.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class ValidationUtils {

    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    public static <T> String validate(Collection<T> collection) {
        for (T t : collection) {
            String msg = validate(t);
            if (msg != null) {
                return msg;
            }
        }
        return null;
    }

    public static <T> String validate(T t) {
        try {
            Validator validator = FACTORY.getValidator();
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                if (Objects.nonNull(constraintViolation.getMessageTemplate())) {
                    return constraintViolation.getMessageTemplate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
