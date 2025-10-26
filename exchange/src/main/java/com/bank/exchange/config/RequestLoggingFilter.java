package com.bank.exchange.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("FILTER CALLED for: {} {}", request.getMethod(), request.getRequestURI());
        // Логируем все GET запросы для диагностики
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            log.info("=== GET REQUEST ===");
            log.info("URI: {}", request.getRequestURI());
            log.info("Query: {}", request.getQueryString());

            // Логируем все заголовки
            log.info("Headers:");
            Collections.list(request.getHeaderNames()).forEach(headerName ->
                    log.info("  {}: {}", headerName, request.getHeader(headerName)));

            String authHeader = request.getHeader("Authorization");
            log.info("Authorization header: {}", authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("BEARER TOKEN FOUND: {}", token);
            } else {
                log.info("NO BEARER TOKEN FOUND");
            }
            log.info("=== END GET ===");
        }

        filterChain.doFilter(request, response);
    }

}