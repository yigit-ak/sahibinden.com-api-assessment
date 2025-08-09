package com.sahibinden.codecase.utility;

import com.sahibinden.codecase.entity.Category;
import com.sahibinden.codecase.entity.Classified;

import java.security.NoSuchAlgorithmException;

public final class DuplicateKey {

    public static String of(Category c, String title, String desc) throws NoSuchAlgorithmException {
        String payload = c.name() + "|" +
                TextNormalizer.normalize(title) + "|" +
                TextNormalizer.normalize(desc);
        byte[] sha = java.security.MessageDigest.getInstance("SHA-256")
                .digest(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // Base64url is compact (44 chars) and index-friendly
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(sha);
    }

    public static String of(Classified classified) throws NoSuchAlgorithmException {
        return of(classified.getCategory(), classified.getTitle(), classified.getDetail());
    }

}
