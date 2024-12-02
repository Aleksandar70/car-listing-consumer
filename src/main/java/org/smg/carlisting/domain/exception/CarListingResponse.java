package org.smg.carlisting.domain.exception;

import lombok.Data;
import org.smg.carlisting.domain.model.CarListing;

import java.util.List;

/**
 * This class represents the response structure for car listing queries.
 * It encapsulates the result of the query, indicating whether the operation
 * was successful, the data resulting from the query (list of car listings),
 * and any error message if applicable.
 * <p>
 * The response includes a 'valid' flag indicating the success or failure of
 * the operation, a 'data' field containing a list of {@link CarListing} objects
 * representing the car listings, and an 'error' field with a message describing
 * any error that occurred during the operation.
 */
@Data
public class CarListingResponse {
    private boolean valid;
    private List<CarListing> data;
    private String error;

    public CarListingResponse(boolean valid, List<CarListing> data, String error) {
        this.valid = valid;
        this.data = data;
        this.error = error;
    }
}
