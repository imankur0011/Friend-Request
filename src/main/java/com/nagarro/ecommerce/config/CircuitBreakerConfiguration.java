package com.nagarro.ecommerce.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // The failure rate threshold at which the circuit should open (50%)
                .waitDurationInOpenState(Duration.ofMillis(5000)) // Time to wait before transitioning to half-open state (5 seconds)
                .permittedNumberOfCallsInHalfOpenState(2) // The number of calls allowed in half-open state (2)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // Count-based sliding window
                .slidingWindowSize(10) // The number of calls considered for failure rate calculation (10)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }
}
