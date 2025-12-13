package com.bloodbowlclub.shared.shared.cloudinary_url;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CloudinaryUrlValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CloudinaryUrlConstraint {
    String message() default  "{cloudinary_url.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
