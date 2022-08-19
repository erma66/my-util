package com.erma.util.validate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Iterator;
import java.util.Set;

/**
 * @Date 2022/6/27 16:04
 * @Created by erma66
 */
public class ValidatorUtils {
    private ValidatorUtils() {
    }

    private static final Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     * @throws IllegalArgumentException 校验不通过，则报IllegalArgumentException异常
     */
    public static void validateEntity(Object object, Class<?>... groups) throws IllegalArgumentException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);

        if (!constraintViolations.isEmpty()) {
            Iterator<ConstraintViolation<Object>> iterator = constraintViolations.iterator();
            StringBuilder msg = new StringBuilder();
            while (iterator.hasNext()) {
                ConstraintViolation<Object> constraint = iterator.next();
                msg.append(constraint.getPropertyPath()).append(constraint.getMessage()).append(',');
            }
            throw new IllegalArgumentException(msg.substring(0, msg.toString().lastIndexOf(',')));
        }
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @throws IllegalArgumentException 校验不通过，则报IllegalArgumentException异常
     */
    public static void validateEntity(Object object) throws IllegalArgumentException {
        validateEntity(object, Default.class);
    }
}
