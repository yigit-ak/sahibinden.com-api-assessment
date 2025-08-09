package com.sahibinden.codecase.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ClassifiedValidationTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static boolean hasViolation(Set<ConstraintViolation<Classified>> v, String field, String containsMsg) {
        return v.stream().anyMatch(cv ->
                cv.getPropertyPath().toString().equals(field) &&
                        (containsMsg == null || cv.getMessage().contains(containsMsg))
        );
    }

    private Classified valid() {
        return Classified.builder()
                .id(123456789)
                .title("3+1 Daire İlanı - Şehir Merkezi") // starts with letter/digit, len 10–50
                .detail("Merkezde geniş, aydınlık daire. Okullara ve toplu taşımaya yakın.")
                .category(Category.REAL_ESTATE)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void validEntity_passes() {
        var c = valid();
        var violations = validator.validate(c);
        assertTrue(violations.isEmpty(), "Expected no violations for a valid entity");
    }

    // --- title ---

    @Test
    void title_blank_fails() {
        var c = valid();
        c.setTitle("   ");
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "title", "must not be blank"));
    }

    @Test
    void title_tooShort_fails() {
        var c = valid();
        c.setTitle("Too short"); // 9 chars
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "title", "size must be between 10 and 50"));
    }

    @Test
    void title_tooLong_fails() {
        var c = valid();
        c.setTitle("X".repeat(51));
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "title", "size must be between 10 and 50"));
    }

    @Test
    void title_mustStartWithLetterOrDigit_fails() {
        var c = valid();
        c.setTitle("-Başlık tire ile"); // starts with punctuation
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "title", "Title must start with a letter or digit"));
    }

    @Test
    void title_unicodeLetter_start_ok() {
        var c = valid();
        c.setTitle("İlan başlığı — merkezde"); // starts with Unicode letter
        var v = validator.validate(c);
        assertTrue(v.isEmpty());
    }

    // --- detail ---

    @Test
    void detail_blank_fails() {
        var c = valid();
        c.setDetail("");
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "detail", "must not be blank"));
    }

    @Test
    void detail_tooShort_fails() {
        var c = valid();
        c.setDetail("Kısa açıklama."); // < 20
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "detail", "size must be between 20 and 200"));
    }

    // --- enums ---

    @Test
    void category_null_fails() {
        var c = valid();
        c.setCategory(null);
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "category", "must not be null"));
    }

    @Test
    void status_null_fails() {
        var c = valid();
        c.setStatus(null);
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "status", "must not be null"));
    }
}
