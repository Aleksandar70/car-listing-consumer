package org.smg.carlisting.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.smg.carlisting.domain.model.CarListing;

/**
 * Represents an event related to a car listing in the application.
 * <p>
 * This class encapsulates the details of an event occurring on a car listing,
 * such as creation, update, or deletion. It includes information about the
 * event type and the car listing involved in the event.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarListingEvent {
    private String id;
    private EventType eventType;
    private CarListing carListing;
}
