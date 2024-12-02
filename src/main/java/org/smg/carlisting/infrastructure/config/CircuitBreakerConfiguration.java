package org.smg.carlisting.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;

/**
 * Configuration class for setting up circuit breakers with Resilience4j.
 * <p>
 * This class provides a bean definition for the circuit breaker registry
 * which holds the configuration for circuit breakers used in the application.
 * The configuration defines parameters such as failure rate threshold, wait
 * duration in open state, size of the sliding window, and exceptions to ignore.
 * </p>
 */
@Configuration
public class CircuitBreakerConfiguration {

    /**
     * Creates and configures a CircuitBreakerRegistry bean.
     * <p>
     * The registry contains configurations for circuit breakers.
     * A custom CircuitBreakerConfig is created with specific parameters
     * such as failure rate threshold, wait duration in the open state,
     * sliding window type and size, minimum number of calls, and exceptions
     * to ignore. This configuration helps in controlling the behavior of
     * circuit breakers in the application.
     * </p>
     *
     * @return CircuitBreakerRegistry with custom configuration.
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)// Percentage of failures to open the circuit
                .waitDurationInOpenState(Duration.ofSeconds(60))// Time circuit breaker stays open
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)//Fixed Number of Calls to determine when to open or close
                .slidingWindowSize(10)// Last 10 calls to measure the failure rate
                .recordExceptions(IOException.class, TimeoutException.class, DataAccessResourceFailureException.class, ConnectException.class)//A list of exceptions that will consider as failures
                .minimumNumberOfCalls(3)//The minimum number of calls required before the Circuit Breaker can calculate the failure rate and decide whether to open
                .permittedNumberOfCallsInHalfOpenState(3)//The number of calls allowed when the Circuit Breaker is in a half-open state to test if the ES is still unavailable
                .build();

        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }
}
