//package com.bank.transfer;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//@Component
//@Order(1)
//public class LoggingFilter extends OncePerRequestFilter {
//    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        logger.info("LoggingFilter called for URI: {}", request.getRequestURI());
//        logger.info("Method: {}, Content-Type: {}", request.getMethod(), request.getContentType());
//
//        // Читаем тело запроса напрямую
//        StringBuilder requestBody = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                requestBody.append(line);
//            }
//        }
//        if (requestBody.length() > 0) {
//            logger.info("Raw JSON payload for {}: {}", request.getRequestURI(), requestBody.toString());
//        } else {
//            logger.warn("No request body for {}", request.getRequestURI());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}