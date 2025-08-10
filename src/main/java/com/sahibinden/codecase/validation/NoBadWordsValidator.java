package com.sahibinden.codecase.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoBadWordsValidator implements ConstraintValidator<NoBadWords, String> {
    private final BadWordDetector badWordDetector;

    public NoBadWordsValidator(BadWordDetector badWordDetector) {
        this.badWordDetector = badWordDetector;
    }

    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value == null || !badWordDetector.contains(value);
    }
}
