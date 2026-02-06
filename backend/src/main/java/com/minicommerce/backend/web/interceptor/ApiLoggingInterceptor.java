package com.minicommerce.backend.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to log API request information using MDC (Mapped Diagnostic Context).
 * Records: method, URI, start time, end time, duration, and status code.
 */
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

  private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
  private static final String START_TIME_ATTRIBUTE = "startTime";
  private static final String MDC_METHOD = "method";
  private static final String MDC_URI = "uri";
  private static final String MDC_START_TIME = "startTime";
  private static final String MDC_END_TIME = "endTime";
  private static final String MDC_DURATION = "duration";
  private static final String MDC_STATUS_CODE = "statusCode";

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler
  ) {
    Instant startTime = Instant.now();
    request.setAttribute(START_TIME_ATTRIBUTE, startTime);

    // Put request information into MDC
    MDC.put(MDC_METHOD, request.getMethod());
    MDC.put(MDC_URI, request.getRequestURI());
    MDC.put(MDC_START_TIME, startTime.toString());

    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      Exception ex
  ) {
    try {
      Instant startTime = (Instant) request.getAttribute(START_TIME_ATTRIBUTE);
      Instant endTime = Instant.now();

      if (startTime != null) {
        Duration duration = Duration.between(startTime, endTime);
        long durationMs = duration.toMillis();

        // Update MDC with completion information
        MDC.put(MDC_END_TIME, endTime.toString());
        MDC.put(MDC_DURATION, String.valueOf(durationMs));
        MDC.put(MDC_STATUS_CODE, String.valueOf(response.getStatus()));

        // Log the API request
        log.info("API Request completed: {} {} - Status: {} - Duration: {}ms",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            durationMs);

        // Log error if exception occurred
        if (ex != null) {
          log.error("Exception occurred during request processing", ex);
        }
      }
    } finally {
      // Always clear MDC to prevent memory leaks and cross-request contamination
      MDC.clear();
    }
  }
}

