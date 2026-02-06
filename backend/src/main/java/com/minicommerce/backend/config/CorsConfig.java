package com.minicommerce.backend.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsConfig implements WebMvcConfigurer {

  private List<String> allowedOrigins = List.of("http://localhost:10086", "http://localhost:3000");

  public void setAllowedOrigins(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
        .allowedMethods("GET", "POST", "OPTIONS")
        .allowedHeaders("Content-Type", "Idempotency-Key", "Authorization")
        .exposedHeaders("Content-Type")
        .allowCredentials(true)
        .maxAge(3600);
  }
}


