package com.minicommerce.backend.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Rate limiting interceptor using token bucket algorithm.
 * Limits requests per IP address to prevent abuse.
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

  @Value("${app.rate-limit.requests-per-minute:60}")
  private int requestsPerMinute;

  @Value("${app.rate-limit.burst-size:10}")
  private int burstSize;

  // Token bucket per IP: {IP -> {tokens: count, lastRefill: timestamp}}
  private static class TokenBucket {
    private int tokens;
    private long lastRefillTime;

    TokenBucket(int initialTokens) {
      this.tokens = initialTokens;
      this.lastRefillTime = System.currentTimeMillis();
    }
  }

  private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {
    // Skip rate limiting for excluded paths
    String path = request.getRequestURI();
    if (path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.equals("/swagger-ui.html") ||
        path.equals("/error") ||
        path.equals("/favicon.ico")) {
      return true;
    }

    String clientIp = getClientIp(request);
    TokenBucket bucket = buckets.computeIfAbsent(
        clientIp,
        k -> new TokenBucket(burstSize)
    );

    // Refill tokens based on time elapsed
    long now = System.currentTimeMillis();
    long elapsed = now - bucket.lastRefillTime;
    long tokensToAdd = (elapsed * requestsPerMinute) / 60000; // tokens per millisecond

    if (tokensToAdd > 0) {
      bucket.tokens = Math.min(burstSize, bucket.tokens + (int) tokensToAdd);
      bucket.lastRefillTime = now;
    }

    // Check if request can be processed
    if (bucket.tokens > 0) {
      bucket.tokens--;
      return true;
    }

    // Rate limit exceeded
    log.warn("Rate limit exceeded for IP: {}, path: {}", clientIp, path);
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType("application/json");
    response.getWriter().write(
        "{\"type\":\"about:blank\"," +
            "\"title\":\"Too Many Requests\"," +
            "\"status\":429," +
            "\"detail\":\"Rate limit exceeded. Please try again later.\"}"
    );
    return false;
  }

  /**
   * Get client IP address from request, considering proxy headers.
   */
  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    // If multiple IPs, take the first one
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip != null ? ip : "unknown";
  }
}

