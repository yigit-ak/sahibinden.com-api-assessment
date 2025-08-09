package com.sahibinden.codecase.utility;

import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateKeyTest {

    @Test
    void sameInputs_produceSameKey() throws Exception {
        String k1 = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Some longer detail text here...");
        String k2 = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Some longer detail text here...");
        assertEquals(k1, k2);
    }

    @Test
    void differentCategory_changesKey() throws Exception {
        String a = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Detail...");
        String b = DuplicateKey.of(Category.AUTOMOTIVE, "Nice Title 123", "Detail...");
        assertNotEquals(a, b);
    }

    @Test
    void differentTitle_changesKey() throws Exception {
        String a = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Detail...");
        String b = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 124", "Detail...");
        assertNotEquals(a, b);
    }

    @Test
    void differentDetail_changesKey() throws Exception {
        String a = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Detail A...");
        String b = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Detail B...");
        assertNotEquals(a, b);
    }

    @Test
    void base64url_format_isUrlSafe_noPadding_fixedLength() throws Exception {
        String k = DuplicateKey.of(Category.REAL_ESTATE, "Nice Title 123", "Some longer detail text...");
        // URL-safe Base64 uses A–Z, a–z, 0–9, '-' and '_'
        assertTrue(k.matches("^[A-Za-z0-9_-]+$"), "Not URL-safe Base64");
        assertFalse(k.contains("="), "Should be without padding");
        assertEquals(43, k.length(), "SHA-256 base64url without padding must be 43 chars");
    }

    @Test
    void of_Classified_delegatesCorrectly() throws Exception {
        Classified c = Classified.builder()
                .category(Category.REAL_ESTATE)
                .status(Status.ACTIVE)
                .title("Nice Title 123")
                .detail("Some longer detail text...")
                .build();

        String expected = DuplicateKey.of(c.getCategory(), c.getTitle(), c.getDetail());
        String actual = DuplicateKey.of(c);
        assertEquals(expected, actual);
    }

    @Test
    void normalization_equivalentInputs_sameKey() throws Exception {
        String a = DuplicateKey.of(Category.REAL_ESTATE, "  İLAN  BAŞLIĞI ", "Detay\tmetni");
        String b = DuplicateKey.of(Category.REAL_ESTATE, "ilan başlığı", "Detay  metni");
        assertEquals(a, b);
    }
}
