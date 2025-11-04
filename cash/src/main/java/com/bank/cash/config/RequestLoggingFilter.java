package com.bank.cash.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        log.info("=== FILTER: BEFORE CONTROLLER ===");
        log.info("Method: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        log.info("Content-Type: {}", httpRequest.getContentType());

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) &&
                httpRequest.getContentType() != null &&
                httpRequest.getContentType().contains("application/json")) {

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);

            // Продолжаем цепочку фильтров
            chain.doFilter(wrappedRequest, response);

            // После выполнения читаем тело из кэша
            byte[] body = wrappedRequest.getContentAsByteArray();
            if (body.length > 0) {
                String bodyStr = new String(body, StandardCharsets.UTF_8);
                log.info("Request Body: {}", bodyStr);
            }

        } else {
            chain.doFilter(request, response);
        }
    }
}
