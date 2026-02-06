package com.minicommerce.backend.config;

import com.minicommerce.backend.web.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final ApiLoggingInterceptor apiLoggingInterceptor;

  public WebMvcConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
    this.apiLoggingInterceptor = apiLoggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(apiLoggingInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/error",
            "/favicon.ico"
        );
  }
}

