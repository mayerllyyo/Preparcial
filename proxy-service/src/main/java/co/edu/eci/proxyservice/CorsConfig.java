package co.edu.eci.proxyservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // In production, replace "*" with the actual client origin (e.g. "http://client-host")
        String allowedOrigin = System.getenv("PROXY_ALLOWED_ORIGIN");
        if (allowedOrigin == null || allowedOrigin.isBlank()) {
            allowedOrigin = "*";
        }
        registry.addMapping("/proxy/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("Accept", "Content-Type")
                .maxAge(3600);
    }
}
