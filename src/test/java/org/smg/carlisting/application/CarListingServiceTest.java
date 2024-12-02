package org.smg.carlisting.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smg.carlisting.domain.event.CarListingEvent;
import org.smg.carlisting.domain.event.EventType;
import org.smg.carlisting.domain.model.CarListing;
import org.smg.carlisting.infrastructure.elasticsearch.ElasticCarListingRepository;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarListingServiceTest {

    private static final String CAR_LISTING_ID = "test_id";

    @InjectMocks
    private CarListingService carListingService;

    @Mock
    private ElasticCarListingRepository elasticCarListingRepository;

    public CarListingSearchCriteria criteria;
    public Pageable pageable;
    private CarListingEvent carListingEvent;
    private CarListing carListing;

    @BeforeEach
    public void setup() {
        carListing = new CarListing();
        carListing.setId(CAR_LISTING_ID);
        carListingEvent = new CarListingEvent();
        carListingEvent.setCarListing(carListing);
    }

    @Test
    public void testProcessCarListingCreate() {
        carListingEvent.setEventType(EventType.CREATE);
        carListingService.processCarListing(carListingEvent);
        verify(elasticCarListingRepository).createOrUpdateElasticsearchIndex(carListing);
    }

    @Test
    public void testProcessCarListingUpdate() {
        carListingEvent.setEventType(EventType.UPDATE);
        carListingService.processCarListing(carListingEvent);
        verify(elasticCarListingRepository).createOrUpdateElasticsearchIndex(carListing);
    }

    @Test
    public void testProcessCarListingDelete() {
        carListingEvent.setEventType(EventType.DELETE);
        carListingService.processCarListing(carListingEvent);
        verify(elasticCarListingRepository).deleteFromElasticsearchIndex(CAR_LISTING_ID);
    }

    @Test
    public void testSearchCarListings() {
        List<CarListing> carListings = Collections.singletonList(new CarListing());
        when(elasticCarListingRepository.searchCarListings(criteria, pageable)).thenReturn(carListings);

        List<CarListing> result = carListingService.searchCarListings(criteria, pageable);

        assertEquals(carListings, result);
        verify(elasticCarListingRepository).searchCarListings(criteria, pageable);
    }
}