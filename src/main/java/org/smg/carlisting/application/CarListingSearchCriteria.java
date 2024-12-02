package org.smg.carlisting.application;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.smg.carlisting.domain.validator.CarListingYear;

/**
 * Represents the search criteria for querying car listings.
 * <p>
 * This class is used to encapsulate the criteria used for searching car listings,
 * such as make, model, year, price range, and color. It is typically used to
 * filter and fetch relevant car listings based on user preferences or search inputs.
 * </p>
 */
@Data
public class CarListingSearchCriteria {
    private String make;

    private String model;

    @CarListingYear
    private Integer year;

    @Positive
    private Double minPrice;

    @Positive
    private Double maxPrice;

    private String color;
}
