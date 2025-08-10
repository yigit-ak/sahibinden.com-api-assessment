package com.sahibinden.codecase.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BadWordDetectorTest {

    @Autowired
    BadWordDetector detector;

    @Test
    void loadsListAndFindsWholeWords_caseInsensitive() {
        assertTrue(detector.contains("Bu yazıda BADWORD1 var."));
        assertTrue(detector.contains("Bunu bAdword2 diyerek geçiştirme"));
        assertTrue(detector.contains("Bir badWORD3 söyleyeceğim."));
    }

    @Test
    void ignoresPunctuationAndMatchesWholeTokensOnly() {
        assertTrue(detector.contains("badword4, noktalama ile de yakalanır!"));
        assertFalse(detector.contains("nonbadword1 başka bir kelime"));
        assertFalse(detector.contains("Temiz metin, sorun yok."));
    }

    @Test
    void nullOrBlankIsSafe() {
        assertFalse(detector.contains(null));
        assertFalse(detector.contains(""));
        assertFalse(detector.contains("   "));
    }
}
