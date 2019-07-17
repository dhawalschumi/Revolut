/**
 * 
 */
package com.revolut.transfers.service;

import com.revolut.transfers.account.Account;

/**
 * @author dhawp
 * 
 * This service will take care of transfers between two accounts.
 */
public interface TransferService {

	public Account transfer(final Account fromAccount, final Account toAccount);

}
