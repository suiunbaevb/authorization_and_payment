// TooManyAttemptsException.java
package com.example.payment_api.exception;

import org.springframework.security.authentication.LockedException;

/**
 * Thrown when a user exceeds the max allowed login attempts
 */
public class TooManyAttemptsException extends LockedException {
    public TooManyAttemptsException() {
        super("Too many login attempts, account is temporarily locked");
    }
}
