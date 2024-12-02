package org.smg.carlisting.infrastructure.elasticsearch;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smg.carlisting.common.Constants;
import org.smg.carlisting.domain.model.CarListing;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticCarListingRepositoryTest {

    private static final String ID = "123";
    private static final String INDEX_NOT_FOUND = "Index not found";

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    private CarListing carListing;

    private ElasticCarListingRepository elasticCarListingRepository;


    @BeforeEach
    public void setup() {
        carListing = new CarListing();
        carListing.setId(ID);
        when(circuitBreakerRegistry.circuitBreaker(Constants.ELASTICSEARCH_CIRCUIT_BREAKER)).thenReturn(circuitBreaker);
        elasticCarListingRepository = new ElasticCarListingRepository(elasticsearchOperations, circuitBreakerRegistry);
    }

    @Test
    public void testCreateOrUpdateElasticsearchIndexSuccess() {
        elasticCarListingRepository.createOrUpdateElasticsearchIndex(carListing);
        verify(elasticsearchOperations).save(carListing);
    }

    @Test
    public void testCreateOrUpdateNoIndex() {
        NoSuchIndexException exception = new NoSuchIndexException(INDEX_NOT_FOUND);
        doThrow(exception).when(elasticsearchOperations).save(carListing, CarListing.class);

        assertDoesNotThrow(() -> elasticCarListingRepository.createOrUpdateElasticsearchIndex(carListing));
    }

    @Test
    public void testDeleteSuccess() {
        elasticCarListingRepository.deleteFromElasticsearchIndex(ID);
        verify(elasticsearchOperations).delete(ID, CarListing.class);
    }

    @Test
    public void testDeleteNoIndex() {
        NoSuchIndexException exception = new NoSuchIndexException(INDEX_NOT_FOUND);
        doThrow(exception).when(elasticsearchOperations).delete(ID, CarListing.class);

        assertDoesNotThrow(() -> elasticCarListingRepository.deleteFromElasticsearchIndex(ID));
    }
}