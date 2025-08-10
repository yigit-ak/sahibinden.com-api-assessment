package com.sahibinden.codecase.validation;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NoBadWordsTest {

    @Autowired
    Validator validator;

    @Test
    void raisesViolationWhenBadWordPresent() {
        Set<ConstraintViolation<Dto>> v = validator.validate(new Dto("Bu başlıkta badword2 var"));
        assertFalse(v.isEmpty());
        assertEquals("Contains prohibited words", v.iterator().next().getMessage());
    }

    @Test
    void passesWhenClean() {
        Set<ConstraintViolation<Dto>> v = validator.validate(new Dto("Temiz ve güvenli başlık"));
        assertTrue(v.isEmpty());
    }

    static class Dto {
        @NoBadWords
        String title;

        Dto(String title) {
            this.title = title;
        }
    }
}
