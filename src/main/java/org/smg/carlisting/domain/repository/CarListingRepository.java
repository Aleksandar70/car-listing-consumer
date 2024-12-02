package org.smg.carlisting.domain.repository;

import org.smg.carlisting.application.CarListingSearchCriteria;
import org.smg.carlisting.domain.model.CarListing;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface for repository operations related to Car Listings.
 * <p>
 * This interface defines the methods for interacting with the underlying data store
 * for CarListing entities. It includes operations for searching, creating, updating,
 * and deleting car listings.
 * </p>
 */
public interface CarListingRepository {
    List<CarListing> searchCarListings(CarListingSearchCriteria searchCriteria, Pageable pageable);

    void createOrUpdateElasticsearchIndex(CarListing carListing);

    void deleteFromElasticsearchIndex(String carListingId);
}
