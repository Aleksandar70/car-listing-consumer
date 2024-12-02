package org.smg.carlisting.common;

/**
 * Class containing constants used throughout the Car Listing application.
 * <p>
 * This class defines a series of static final fields representing various
 * constants, such as field names and configuration values, that are used
 * across different parts of the application.
 * </p>
 */
public class Constants {
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String YEAR = "year";
    public static final String MIN_PRICE = "minPrice";
    public static final String MAX_PRICE = "maxPrice";
    public static final String COLOR = "color";
    public static final String CACHE_NAME = "carListings";
    public static final String ELASTICSEARCH_CIRCUIT_BREAKER = "elasticsearchCircuitBreaker";
    public static final String MESSAGE_EXCEPTION_ELASTICSEARCH = "Error during communication to Elasticsearch {}";
    public static final String CIRCUIT_BREAKER_IS_OPEN = "Circuit breaker is open: {}";

}
