package com.sahibinden.codecase.utility;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {
    private static final Locale TR = new Locale("tr", "TR");

    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC)
                .replaceAll("\\p{P}+", " ")
                .replaceAll("\\s+", " ") // keep a few safe punctuations
                .trim();
        return n.toLowerCase(TR);
    }
}
