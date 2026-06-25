package com.example.demo.config;

import com.example.demo.security.TokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration:
 *  - CORS so the React frontend (localhost:5173 / 3000) can call the API.
 *  - Registers the token interceptor that protects the Todo endpoints.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String[] allowedOrigins;

    private final TokenAuthInterceptor tokenAuthInterceptor;

    public CorsConfig(TokenAuthInterceptor tokenAuthInterceptor) {
        this.tokenAuthInterceptor = tokenAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Only the Todo endpoints require a valid login token.
        registry.addInterceptor(tokenAuthInterceptor)
                .addPathPatterns("/api/todos", "/api/todos/**");
    }
}
