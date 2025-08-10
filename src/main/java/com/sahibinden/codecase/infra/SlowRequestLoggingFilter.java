package com.sahibinden.codecase.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SlowRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("slow-requests");

    private final long thresholdMs = 5;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            if (tookMs > thresholdMs) {
                String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
                log.warn("slow-request method={} uri={} pattern={} status={} tookMs={} ua='{}' ip={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        pattern,
                        response.getStatus(),
                        tookMs,
                        Optional.ofNullable(request.getHeader("User-Agent")).orElse("-"),
                        request.getRemoteAddr());
            }
        }
    }
}
