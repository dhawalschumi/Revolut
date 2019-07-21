/**
 * 
 */
package com.revolut.balance.service;

import java.math.BigDecimal;

import com.revolut.transfers.account.Account;
import com.revolut.transfers.account.Balance;

/**
 * @author Dhawal Patel
 *
 */
public interface BalanceService {

	public Balance getAccountBalance(final long accountId) throws Exception;

	public void updateAccountBalance(final Account fromAccount, final Account toAccount, final BigDecimal amount)
			throws Exception;

}