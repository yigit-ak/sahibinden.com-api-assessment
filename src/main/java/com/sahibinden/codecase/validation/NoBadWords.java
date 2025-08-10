package com.sahibinden.codecase.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoBadWordsValidator.class)
public @interface NoBadWords {
    String message() default "Contains prohibited words";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

