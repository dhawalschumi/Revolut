/**
 * 
 */
package com.revolut.transfers.exception;

/**
 * @author Dhawal Patel
 *
 */
public class BalanceNotFoundException extends Exception {

	private static final long serialVersionUID = -7720707980423982463L;

	public BalanceNotFoundException(final String message) {
		super(message);
	}

}
