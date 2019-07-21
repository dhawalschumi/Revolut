package com.revolut.transfers.exception;

/**
 * @author Dhawal Patel
 *
 */
public class ConcurrentUpdateException extends Exception {

	private static final long serialVersionUID = 1615217796540032518L;

	public ConcurrentUpdateException(final String message) {
		super(message);
	}
}
