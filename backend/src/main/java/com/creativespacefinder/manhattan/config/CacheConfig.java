package com.creativespacefinder.manhattan.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
<<<<<<< Updated upstream
                .expireAfterWrite(2, TimeUnit.HOURS)  // Keep cached items for 2 hours
                .maximumSize(500);                   // Maximum 500 entries in cache
=======
                .expireAfterWrite(24, TimeUnit.HOURS)  // 24-hour cache (matches daily schedule)
                .maximumSize(1000)                     // Plenty of room for all combinations
                .recordStats();                        // Enable cache statistics for monitoring
>>>>>>> Stashed changes
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
<<<<<<< Updated upstream
        CaffeineCacheManager manager = new CaffeineCacheManager("mlPredictions");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
=======
        CaffeineCacheManager manager = new CaffeineCacheManager("recommendations");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
>>>>>>> Stashed changes
