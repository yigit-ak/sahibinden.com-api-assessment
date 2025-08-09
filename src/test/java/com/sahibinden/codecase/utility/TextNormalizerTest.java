package com.sahibinden.codecase.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextNormalizerTest {

    @Test
    void nullYieldsEmptyString() {
        assertEquals("", TextNormalizer.normalize(null));
    }

    @Test
    void trimsAndCollapsesWhitespace() {
        assertEquals("hello world", TextNormalizer.normalize("  Hello   \n  World\t\t "));
        assertEquals("a b c", TextNormalizer.normalize("a   b    c"));
    }

    @Test
    void unicodeNFKCNormalization() {
        // ﬁ ligature → fi
        assertEquals("fi", TextNormalizer.normalize("ﬁ"));
        // Combining accent → composed form
        assertEquals("café", TextNormalizer.normalize("Cafe\u0301"));
        // Fullwidth to ASCII, then lowercase
        assertEquals("abc123", TextNormalizer.normalize("ＡＢＣ１２３"));
        // Circled digits → digits
        assertEquals("12", TextNormalizer.normalize("①②"));
    }

    @Test
    void turkishLowercasingRules() {
        // Turkish I → ı (dotless i)
        assertEquals("ı", TextNormalizer.normalize("I"));
        // Turkish İ → i + combining dot (U+0069 U+0307); this is what Java returns for TR locale
        assertEquals("i", TextNormalizer.normalize("İ"));
        // Common Turkish letters
        assertEquals("ışüğöç", TextNormalizer.normalize("IŞÜĞÖÇ"));
        assertEquals("ığişüöç", TextNormalizer.normalize("IĞİŞÜÖÇ"));
    }

    @Test
    void stripsMostPunctuationButKeepsWhitelistedOnes() {
        // General stripping
        assertEquals("hello world", TextNormalizer.normalize("Hello, world!"));
        assertEquals("100", TextNormalizer.normalize("100%"));
        assertEquals("", TextNormalizer.normalize("!!!"));
        // Keeps hyphen, underscore, straight apostrophe, and right single quote
        assertEquals("co op", TextNormalizer.normalize("CO-OP"));
        assertEquals("can t", TextNormalizer.normalize("Can't?"));
        assertEquals("o connor", TextNormalizer.normalize("O’Connor"));
        assertEquals("foo bar", TextNormalizer.normalize("Foo_Bar"));
        // Em dash is removed
        assertEquals("a b", TextNormalizer.normalize("a—b"));
        // If there’s no space around punctuation, words stick together (by design)
        assertEquals("hello world", TextNormalizer.normalize("hello,world"));
    }

    @Test
    void idempotentForNormalizedOutput() {
        String[] samples = {
                null,
                "",
                "  Hello   World  ",
                "ﬁsh & chips!!!",
                "Cafe\u0301",
                "İSTANBUL",
                "CO-OP",
                "a—b",
                "Foo_Bar",
                "①②③ test"
        };
        for (String s : samples) {
            String once = TextNormalizer.normalize(s);
            assertEquals(once, TextNormalizer.normalize(once));
        }
    }
}
