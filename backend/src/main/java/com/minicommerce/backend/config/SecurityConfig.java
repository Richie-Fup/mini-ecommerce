package com.minicommerce.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Security headers configuration to protect against common vulnerabilities.
 * Note: For production, consider using Spring Security for more comprehensive protection.
 */
@Configuration
public class SecurityConfig {

  @Bean
  public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
    FilterRegistrationBean<SecurityHeadersFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new SecurityHeadersFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

  private static class SecurityHeadersFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
      // Prevent clickjacking
      response.setHeader("X-Frame-Options", "DENY");
      
      // Prevent MIME type sniffing
      response.setHeader("X-Content-Type-Options", "nosniff");
      
      // Enable XSS protection
      response.setHeader("X-XSS-Protection", "1; mode=block");

      // Content Security Policy - adjust based on your needs
      // Relaxed for API endpoints that may be called from various origins
      response.setHeader("Content-Security-Policy", "default-src 'self'");
      
      // Referrer Policy
      response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
      
      // Permissions Policy (formerly Feature-Policy)
      response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
      
      filterChain.doFilter(request, response);
    }
  }
}

