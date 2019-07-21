/**
 * 
 */
package com.revolut.account.service;

import com.revolut.transfers.account.Account;

/**
 * @author Dhawal Patel
 *
 */
public interface AccountService {

	public Account getAccountByCustomerId(final long customerId) throws Exception;

}