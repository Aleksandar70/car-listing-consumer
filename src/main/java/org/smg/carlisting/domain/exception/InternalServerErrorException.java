package org.smg.carlisting.domain.exception;

/**
 * Custom exception class that represents an internal server error.
 * This exception is thrown when an internal, unexpected failure occurs
 * in the application, typically indicating that the server encountered
 * an unexpected condition that prevented it from fulfilling the request.
 * <p>
 * This exception can be used in scenarios where a more specific exception
 * is not suitable, and a generic 500 Internal Server Error response needs
 * to be triggered.
 */
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }
}
