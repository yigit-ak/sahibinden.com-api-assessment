package com.sahibinden.codecase.utility;

import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;

public final class DuplicateKey {

    public static String of(Category c, String title, String desc) {
        String payload = c.name() + "|" +
                TextNormalizer.normalize(title) + "|" +
                TextNormalizer.normalize(desc);
        try {
            byte[] sha = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(sha); // 43 chars
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static String of(Classified c) {
        return of(c.getCategory(), c.getTitle(), c.getDetail());
    }

}
