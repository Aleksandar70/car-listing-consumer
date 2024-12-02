package org.smg.carlisting.application;

import lombok.extern.slf4j.Slf4j;
import org.smg.carlisting.domain.event.CarListingEvent;
import org.smg.carlisting.domain.model.CarListing;
import org.smg.carlisting.domain.repository.CarListingRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.smg.carlisting.common.Constants.CACHE_NAME;

/**
 * Service for handling operations related to car listings.
 * <p>
 * This service acts as an intermediary between the controller layer and
 * the data access layer, specifically handling operations related to searching
 * car listings in Elasticsearch.
 * </p>
 */
@Service
@Slf4j
public class CarListingService {

    private final CarListingRepository carListingRepository;

    /**
     * Constructs a new CarListingService with the specified CarListingRepository.
     * <p>
     * This constructor initializes the CarListingService with a repository that is used
     * for data access operations related to car listings. The provided repository is essential
     * for the service to interact with the underlying data store and perform CRUD operations.
     * </p>
     *
     * @param carListingRepository The CarListingRepository used for data access operations on car listings.
     */
    public CarListingService(CarListingRepository carListingRepository) {
        this.carListingRepository = carListingRepository;
    }

    /**
     * Processes the given CarListingEvent based on its event type.
     * <p>
     * Depending on the type of event (CREATE, UPDATE, DELETE), this method will
     * perform the necessary operations on the Elasticsearch index.
     * </p>
     *
     * @param carListingEvent The car listing event to be processed.
     */
    public void processCarListing(CarListingEvent carListingEvent) {
        switch (carListingEvent.getEventType()) {
            case CREATE, UPDATE:
                carListingRepository.createOrUpdateElasticsearchIndex(carListingEvent.getCarListing());
                break;
            case DELETE:
                carListingRepository.deleteFromElasticsearchIndex(carListingEvent.getCarListing().getId());
                break;
            default:
                //In case we decide to insert another Event Type (e.g. OTHER or UNKNOWN)
                log.info("Received an unknown event type: " + carListingEvent.getEventType());
                break;
        }
    }

    /**
     * Searches for car listings based on the provided search criteria and pageable information.
     * <p>
     * This method queries Elasticsearch using the provided search criteria. It supports pagination
     * and sorting as per the pageable parameter. In case of Elasticsearch index not found, it logs
     * an error and returns an empty list.
     * </p>
     *
     * @param query    The criteria used for searching car listings.
     * @param pageable The pagination and sorting information.
     * @return A list of CarListing objects that match the search criteria. Returns an empty list
     * if the Elasticsearch index does not exist or other issues occur during query execution.
     */
    @Cacheable(value = CACHE_NAME)
    public List<CarListing> searchCarListings(CarListingSearchCriteria query, Pageable pageable) {
        return carListingRepository.searchCarListings(query, pageable);
    }
}
