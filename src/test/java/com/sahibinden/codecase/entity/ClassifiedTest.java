package com.sahibinden.codecase.entity;

import com.sahibinden.codecase.utility.DuplicateKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClassifiedTest {

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
                .title("3+1 Daire İlanı - Şehir Merkezi")
                .detail("Merkezde geniş, aydınlık daire. Okullara ve toplu taşımaya yakın.")
                .category(Category.REAL_ESTATE)
                .status(Status.ACTIVE)
                .build();
    }

    // --- Bean Validation ---

    @Test
    void validEntity_passes() {
        var c = valid();
        var violations = validator.validate(c);
        assertTrue(violations.isEmpty());
    }

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
        c.setTitle("-Başlık tire ile");
        var v = validator.validate(c);
        assertTrue(hasViolation(v, "title", "Title must start with a letter or digit"));
    }

    @Test
    void title_unicodeLetter_start_ok() {
        var c = valid();
        c.setTitle("İlan başlığı — merkezde");
        var v = validator.validate(c);
        assertTrue(v.isEmpty());
    }

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

    // --- duplicateKey behavior ---

    @Test
    void builder_sets_duplicateKey_when_fields_present() {
        var c = valid();
        assertNotNull(c.getDuplicateKey(), "duplicateKey should be computed by builder");
        assertTrue(c.getDuplicateKey().matches("^[A-Za-z0-9_-]{43}$"),
                "Must be base64url, no padding, len=43");
    }

    @Test
    void duplicateKey_matchesUtilityMethod() {
        var c = valid();
        String expected = DuplicateKey.of(c);
        assertEquals(expected, c.getDuplicateKey());
    }

    @Test
    void setters_recompute_duplicateKey_on_title_change() {
        var c = valid();
        String k1 = c.getDuplicateKey();
        c.setTitle("3+1 Şehir Merkezi Daire (Geniş ve Aydınlık)");
        String k2 = c.getDuplicateKey();
        assertNotEquals(k1, k2, "Changing title must change duplicateKey");
    }

    @Test
    void setters_recompute_duplicateKey_on_detail_change() {
        var c = valid();
        String k1 = c.getDuplicateKey();
        c.setDetail("Yeni detay: metroya, okullara ve parklara çok yakın, bakımlı bir daire.");
        String k2 = c.getDuplicateKey();
        assertNotEquals(k1, k2, "Changing detail must change duplicateKey");
    }

    @Test
    void setters_recompute_duplicateKey_on_category_change() {
        var c = valid();
        String k1 = c.getDuplicateKey();
        c.setCategory(Category.SHOPPING);
        String k2 = c.getDuplicateKey();
        assertNotEquals(k1, k2, "Changing category must change duplicateKey");
    }

    // --- JPA lifecycle defaults ---

    @Test
    void prePersist_sets_default_status_for_nonShopping() {
        var c = Classified.builder()
                .title("Merkezde geniş daire 3+1")
                .detail("Ulaşım ve okullara yakın, aydınlık ve bakımlı daire.")
                .category(Category.REAL_ESTATE)
                .status(null) // let lifecycle decide
                .build();

        c.prePersistOrUpdate();

        assertEquals(Status.PENDING_APPROVAL, c.getStatus());
        assertNotNull(c.getDuplicateKey());
    }

    @Test
    void prePersist_sets_default_status_active_for_shopping() {
        var c = Classified.builder()
                .title("İndirimli elektronik alışveriş ilanı")
                .detail("Garantili, faturalı ürün. Kargo alıcıya aittir. Detaylar için mesaj atın.")
                .category(Category.SHOPPING)
                .status(null)
                .build();

        c.prePersistOrUpdate();

        assertEquals(Status.ACTIVE, c.getStatus());
        assertNotNull(c.getDuplicateKey());
    }
}
