/**
 * 
 */
package com.revolut.query.service;

import java.math.BigDecimal;

import com.revolut.transfers.account.Account;
import com.revolut.transfers.account.Balance;

/**
 * @author Dhawal Patel
 *
 */
public interface QueryService {

	public Balance executeGetBalanceQuery(final long accountId) throws Exception;

	public void updateAccountBalance(final Account fromAccount, final Account toAccount, final BigDecimal amount)
			throws Exception;

	public Account executeGetAccountForCustomer(long customerId) throws Exception;
}
