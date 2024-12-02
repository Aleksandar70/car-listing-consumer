package org.smg.carlisting.domain.event;

/**
 * Enumeration of the types of events that can occur on a car listing.
 * <p>
 * This enum defines the different kinds of operations or actions that can be
 * performed on a car listing in the application. It is used to specify the nature
 * of an event occurring in the context of car listing management.
 * </p>
 */
public enum EventType {
    CREATE,
    UPDATE,
    DELETE
}
