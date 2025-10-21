package com.bloodbowlclub.lib.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ULIDValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ULIDConstraint {
    String message() default "is not a valid ULID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

