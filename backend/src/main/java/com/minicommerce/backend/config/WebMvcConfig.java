package com.minicommerce.backend.config;

import com.minicommerce.backend.web.interceptor.ApiLoggingInterceptor;
import com.minicommerce.backend.web.interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final ApiLoggingInterceptor apiLoggingInterceptor;
  private final RateLimitInterceptor rateLimitInterceptor;

  public WebMvcConfig(
      ApiLoggingInterceptor apiLoggingInterceptor,
      RateLimitInterceptor rateLimitInterceptor
  ) {
    this.apiLoggingInterceptor = apiLoggingInterceptor;
    this.rateLimitInterceptor = rateLimitInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // Rate limiting should be checked first
    registry.addInterceptor(rateLimitInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/error",
            "/favicon.ico"
        )
        .order(0); // Higher priority

    // API logging interceptor
    registry.addInterceptor(apiLoggingInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/error",
            "/favicon.ico"
        )
        .order(1); // Lower priority
  }
}

