package org.smg.carlisting.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.smg.carlisting.application.CarListingSearchCriteria;
import org.smg.carlisting.application.CarListingService;
import org.smg.carlisting.domain.exception.CarListingResponse;
import org.smg.carlisting.domain.model.CarListing;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest Controller for handling car listing related operations.
 * <p>
 * This controller provides endpoints for searching car listings
 * based on various criteria such as make, model, year, etc.
 * </p>
 */
@RestController
@RequestMapping("/api/car-listings")
public class CarListingController {

    private final CarListingService carListingService;

    /**
     * Constructs a CarListingController with the given CarListingService.
     *
     * @param carListingService The service for car listing operations.
     */
    public CarListingController(CarListingService carListingService) {
        this.carListingService = carListingService;
    }

    /**
     * Searches for car listings based on the given criteria.
     * <p>
     * This endpoint retrieves a list of car listings that match the given search criteria.
     * The search is paginated and can be customized using the pageable parameter.
     * </p>
     *
     * @param query    The search criteria for filtering car listings.
     * @param pageable The pagination information including page number, size and sortBy.
     * @return A ResponseEntity containing a list of car listings matching the search criteria,
     * or an empty list if no matches are found.
     */
    @Operation(summary = "Search car listings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found car listings",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarListing.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<CarListingResponse> searchCarListings(@Valid CarListingSearchCriteria query, Pageable pageable) {
        List<CarListing> carListings = carListingService.searchCarListings(query, pageable);

        return ResponseEntity.ok(new CarListingResponse(true, carListings, null));
    }
}
