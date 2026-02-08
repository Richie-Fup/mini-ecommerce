package com.minicommerce.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Value("${app.cache.products.max-size:100}")
  private int productsCacheMaxSize;

  @Value("${app.cache.products.ttl-seconds:300}")
  private int productsCacheTtlSeconds;

  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("products");
    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(productsCacheMaxSize)
            .expireAfterWrite(productsCacheTtlSeconds, TimeUnit.SECONDS)
            .recordStats() // Enable cache statistics
    );
    return cacheManager;
  }
}

