/*
 * Copyright 2022 Chris Jackson (www.cjack.uk)
 */
package uk.cjack.utilities.exception;

/**
 * Checked Exception for invalid dates
 */
public class InvalidDateException extends Exception {

    /**
     * Creates an exception related to an invalid Date
     *
     * @param message the exception message
     */
    public InvalidDateException( final String message ) {
        super( message );
    }
}
