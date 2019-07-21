/**
 * 
 */
package com.revolut.transfers.exception;

/**
 * @author Dhawal Patel
 *
 */
public class CustomerDoesNotExistsException extends Exception {

	private static final long serialVersionUID = -8548400688579231907L;

	public CustomerDoesNotExistsException(final String message) {
		super(message);
	}

}
