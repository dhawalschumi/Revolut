/**
 * 
 */
package com.revolut.account.service;

import com.revolut.transfers.account.Account;
import com.revolut.transfers.customer.Customer;

/**
 * @author dhawp
 *
 */
public interface AccountService {

	public Account getAccountByCustomerId(final String customerId);

	public Customer getCustomerByAccountId(final long accountId);

}