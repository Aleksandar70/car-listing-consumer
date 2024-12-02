package org.smg.carlisting.infrastructure.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.smg.carlisting.application.CarListingSearchCriteria;
import org.smg.carlisting.domain.exception.InternalServerErrorException;
import org.smg.carlisting.domain.model.CarListing;
import org.smg.carlisting.domain.repository.CarListingRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

import static org.smg.carlisting.common.Constants.CACHE_NAME;
import static org.smg.carlisting.common.Constants.CIRCUIT_BREAKER_IS_OPEN;
import static org.smg.carlisting.common.Constants.COLOR;
import static org.smg.carlisting.common.Constants.ELASTICSEARCH_CIRCUIT_BREAKER;
import static org.smg.carlisting.common.Constants.MAKE;
import static org.smg.carlisting.common.Constants.MAX_PRICE;
import static org.smg.carlisting.common.Constants.MESSAGE_EXCEPTION_ELASTICSEARCH;
import static org.smg.carlisting.common.Constants.MIN_PRICE;
import static org.smg.carlisting.common.Constants.MODEL;
import static org.smg.carlisting.common.Constants.YEAR;

/**
 * Service for managing car listings in Elasticsearch.
 * <p>
 * This service provides functionality for creating, updating, deleting, and
 * searching car listings in an Elasticsearch index. It includes fallback methods
 * for handling errors in communication with Elasticsearch.
 * </p>
 */
@Service
@Slf4j
public class ElasticCarListingRepository implements CarListingRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreaker circuitBreaker;

    public ElasticCarListingRepository(ElasticsearchOperations elasticsearchOperations, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(ELASTICSEARCH_CIRCUIT_BREAKER);
    }

    /**
     * Creates or updates a car listing in the Elasticsearch index. This method
     * is responsible for saving the provided {@code carListing} to the Elasticsearch
     * index. It is wrapped in a circuit breaker to handle partial system failures
     * gracefully. Additionally, this operation triggers the eviction of corresponding
     * entries from the specified cache to maintain consistency. The circuit breaker
     * ensures that, in case of system issues, the operation does not further strain
     * the failing system.
     *
     * @param carListing The {@code CarListing} object to be created or updated in
     *                   the Elasticsearch index. It must not be null.
     */
    @Override
    @CachePut(value = CACHE_NAME)
    public void createOrUpdateElasticsearchIndex(CarListing carListing) {
        executeWithCircuitBreaker(() -> executeCreateOrUpdateElasticsearchIndex(carListing),
                "Car listing is saved with id: " + carListing.getId());
    }

    /**
     * Deletes a car listing from the Elasticsearch index based on its identifier.
     * <p>
     * This method removes the car listing associated with the specified identifier
     * from the Elasticsearch index. It is protected by a circuit breaker to handle
     * partial system failures gracefully. Additionally, this operation causes the
     * eviction of corresponding entries from the specified cache to maintain consistency.
     * The circuit breaker ensures that in case of system failures, the operation
     * does not further strain the failing system.
     * </p>
     * <p>
     * If the circuit breaker is open, indicating ongoing system issues, the method
     * logs an error and avoids making the call to Elasticsearch. If other exceptions
     * are thrown during the operation, they are also logged.
     * </p>
     *
     * @param carListingId The identifier of the car listing to be deleted.
     *                     It must not be null.
     */
    @Override
    @CacheEvict(value = CACHE_NAME)
    public void deleteFromElasticsearchIndex(String carListingId) {
        executeWithCircuitBreaker(() -> executeDeleteFromElasticsearchIndex(carListingId),
                "Car listing with id: " + carListingId + " is deleted from Elasticsearch index.");
    }

    /**
     * Searches for car listings based on given search criteria and pagination settings.
     * This method is protected by a circuit breaker to ensure resilience in the face of
     * potential failures in underlying services or systems. If the circuit breaker is open,
     * indicating a failure state or too many requests, an InternalServerErrorException is thrown.
     *
     * @param searchCriteria The criteria used to filter the car listings, such as make, model, year, etc.
     * @param pageable       The pagination information (page number, size, sorting) for the query.
     * @return A list of {@link CarListing} objects that match the given criteria. The list is
     * subject to pagination based on the provided {@link Pageable} object.
     * @throws InternalServerErrorException if the circuit breaker is in an open state,
     *                                      indicating that the operation cannot be currently processed due to service unavailability.
     */
    @Override
    public List<CarListing> searchCarListings(CarListingSearchCriteria searchCriteria, Pageable pageable) {
        try {
            Supplier<List<CarListing>> carListingSupplier = CircuitBreaker
                    .decorateSupplier(circuitBreaker, () -> executeSearchCarListings(searchCriteria, pageable));
            return carListingSupplier.get();
        } catch (CallNotPermittedException e) {
            log.error(CIRCUIT_BREAKER_IS_OPEN, e.getLocalizedMessage());
            throw new InternalServerErrorException("Circuit Breaker is open!");
        }
    }

    private void executeWithCircuitBreaker(Runnable operation, String successMessage) {
        Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> {
            operation.run();
            log.info(successMessage);
        });

        try {
            runnable.run();
        } catch (CallNotPermittedException e) {
            log.error(CIRCUIT_BREAKER_IS_OPEN, e.getLocalizedMessage());
        } catch (Exception e) {
            log.error(MESSAGE_EXCEPTION_ELASTICSEARCH, e.getLocalizedMessage());
        }
    }

    private void executeCreateOrUpdateElasticsearchIndex(CarListing carListing) {
        try {
            elasticsearchOperations.save(carListing);
            log.info("Car listing is saved with id: {}", carListing.getId());
        } catch (NoSuchIndexException ex) {
            log.error("Executing CREATE/UPDATE event, index is not created for id: {}", carListing.getId());
        }
    }

    private void executeDeleteFromElasticsearchIndex(String carListingId) {
        try {
            elasticsearchOperations.delete(carListingId, CarListing.class);
            log.info("Car listing for id {} is removed", carListingId);
        } catch (NoSuchIndexException ex) {
            log.error("Executing DElETE event, index is not created with id: {}", carListingId);
        }
    }

    private List<CarListing> executeSearchCarListings(CarListingSearchCriteria searchCriteria, Pageable pageable) {
        Query queryCriteria = getQueryCriteria(searchCriteria);
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(queryCriteria)
                .withPageable(pageable)
                .build();

        try {
            SearchHits<CarListing> searchHits = elasticsearchOperations.search(query, CarListing.class);
            return SearchHitSupport.searchPageFor(searchHits, query.getPageable()).getSearchHits()
                    .stream()
                    .map(SearchHit::getContent)
                    .toList();
        } catch (NoSuchIndexException ex) {
            log.error("Executing SEARCH, index is not created yet! {}", ex.getLocalizedMessage());
        }

        return List.of();
    }

    private Query getQueryCriteria(CarListingSearchCriteria searchCriteria) {
        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        String make = searchCriteria.getMake();
        if (make != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(MAKE).query(make)));
        }

        String model = searchCriteria.getModel();
        if (model != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(MODEL).query(model)));
        }

        Integer year = searchCriteria.getYear();
        if (year != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(YEAR).query(year)));
        }

        Double minPrice = searchCriteria.getMinPrice();
        if (minPrice != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(MIN_PRICE).query(minPrice)));
        }

        Double maxPrice = searchCriteria.getMaxPrice();
        if (maxPrice != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(MAX_PRICE).query(maxPrice)));
        }

        String color = searchCriteria.getColor();
        if (color != null) {
            queryBuilder.must(QueryBuilders.match(query -> query.field(COLOR).query(color)));
        }

        return queryBuilder.build()._toQuery();
    }
}
