package com.example.courseportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Bean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Removes Cross-Origin-Opener-Policy header so Firebase Google Auth
     * popup can communicate back with the opener window.
     */
    @Bean
    public OncePerRequestFilter coopHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                filterChain.doFilter(request, response);
                // Allow popup to close and communicate with opener
                response.setHeader("Cross-Origin-Opener-Policy", "unsafe-none");
            }
        };
    }
}
