//package com.bank.exchange.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.Collections;
//
//@Slf4j
//@Component
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        // Создаем обертку для кэширования тела
//        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
//
//        // Логируем метод, URI и заголовки
//        log.info("Request Method: {}", cachedRequest.getMethod());
//        log.info("Request URI: {}", cachedRequest.getRequestURI());
//        log.info("Request Path: {}", cachedRequest.getServletPath());
//        log.info("Request Headers:");
//        Collections.list(cachedRequest.getHeaderNames()).forEach(headerName ->
//                log.info("{}: {}", headerName, cachedRequest.getHeader(headerName)));
//
//        // Логируем тело
//        String body = cachedRequest.getBody();
//        log.info("Request Body: {}", body.length() > 0 ? body : "<empty>");
//
//        // Передаем запрос дальше
//        filterChain.doFilter(cachedRequest, response);
//    }
//
//    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
//        private final String body;
//
//        public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
//            super(request);
//            StringBuilder stringBuilder = new StringBuilder();
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//            }
//            this.body = stringBuilder.toString();
//        }
//
//        public String getBody() {
//            return body;
//        }
//
//        @Override
//        public ServletInputStream getInputStream() throws IOException {
//            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
//            return new ServletInputStream() {
//                @Override
//                public boolean isFinished() {
//                    return byteArrayInputStream.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener readListener) {
//                }
//
//                @Override
//                public int read() throws IOException {
//                    return byteArrayInputStream.read();
//                }
//            };
//        }
//
//        @Override
//        public BufferedReader getReader() throws IOException {
//            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
//        }
//    }
//}