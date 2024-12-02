package org.smg.carlisting.domain.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice to handle exceptions globally for Car Listing related operations.
 * This class intercepts exceptions thrown across the entire application and provides
 * custom responses.
 */
@ControllerAdvice
public class CarListingExceptionHandler {

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<CarListingResponse> handleInternalServerErrorException(InternalServerErrorException ex) {
        return ResponseEntity.internalServerError()
                .body(new CarListingResponse(
                        false,
                        null,
                        ex.getMessage()));
    }
}
