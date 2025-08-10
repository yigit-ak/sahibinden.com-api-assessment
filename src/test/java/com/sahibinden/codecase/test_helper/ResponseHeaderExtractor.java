package com.sahibinden.codecase.test_helper;

import org.springframework.test.web.servlet.MvcResult;

public class ResponseHeaderExtractor {
    public static String extractLocation(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        if (location == null || location.isEmpty()) {
            throw new IllegalStateException("Location header is missing in the response");
        }
        return location;
    }

    public static Long extractId(MvcResult result) {
        String location = extractLocation(result);
        return Long.parseLong(location.replaceAll(".*/", ""));
    }
}
