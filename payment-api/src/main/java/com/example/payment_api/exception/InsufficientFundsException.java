// InsufficientFundsException.java
package com.example.payment_api.exception;

/**
 * Thrown when a payment would overdraw the user’s balance.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient funds to complete this payment");
    }
}
