package com.sahibinden.codecase.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class BadWordDetector {
    private static final Locale TR = new Locale("tr", "TR");
    private final Set<String> badWords = new HashSet<>();

    @Value("${bad-words.file}")
    private String badWordsFile;

    @PostConstruct
    void loadBadWords() throws IOException {
        try (var in = new ClassPathResource(badWordsFile).getInputStream();
             var br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            for (String line; (line = br.readLine()) != null; ) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) badWords.add(line.toLowerCase(TR));
            }
        }
    }

    public boolean contains(String text) {
        if (text == null || text.isBlank()) return false;

        var m = Pattern.compile("[\\p{L}\\p{Nd}]+", Pattern.UNICODE_CHARACTER_CLASS)
                .matcher(text.toLowerCase(TR));

        while (m.find()) if (badWords.contains(m.group())) return true;

        return false;
    }

}
