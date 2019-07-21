/**
 * 
 */
package com.revolut.transfers.exception;

/**
 * @author Dhawal Patel
 *
 */
public class InsufficientBalanceException extends Exception {

	private static final long serialVersionUID = 8593022708781881038L;

	public InsufficientBalanceException(final String message) {
		super(message);
	}
}